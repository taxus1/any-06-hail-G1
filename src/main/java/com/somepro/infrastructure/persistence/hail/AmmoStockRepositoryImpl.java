package com.somepro.infrastructure.persistence.hail;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.somepro.domain.hail.model.AmmoStock;
import com.somepro.domain.hail.repository.AmmoStockRepository;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.infrastructure.config.ReactiveOperatorContext;
import com.somepro.infrastructure.persistence.audit.AuditContextHolder;
import com.somepro.infrastructure.persistence.hail.converter.AmmoStockPoConverter;
import com.somepro.infrastructure.persistence.hail.po.AmmoStockPO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 弹药库存仓储适配器：用 MyBatis-Plus 实现领域仓储端口（基础设施层）。
 *
 * 约定同 demo 模块：所有 DB 调用经 {@link #blocking} 桥接到 boundedElastic；
 * PO 与领域对象在本类里经 AmmoStockPoConverter 互转，不外泄。
 *
 * 入库的核心语义：同一「作业点 + 弹型 + 批次」（表上 uk_stock 唯一）重复入库不另起记录，
 * 而是把本次发数累加到原结存上。
 */
@Repository
public class AmmoStockRepositoryImpl implements AmmoStockRepository {

    private final AmmoStockMapper ammoStockMapper;

    public AmmoStockRepositoryImpl(AmmoStockMapper ammoStockMapper) {
        this.ammoStockMapper = ammoStockMapper;
    }

    @Override
    public Mono<AmmoStock> inbound(AmmoStock inbound) {
        return blocking(() -> {
            AmmoStockPO existing = selectByUniqueKey(inbound);
            if (existing == null) {
                AmmoStockPO po = AmmoStockPoConverter.toPo(inbound);
                po.setId(IdUtil.getSnowflakeNextId());
                try {
                    ammoStockMapper.insert(po);
                    return AmmoStockPoConverter.toDomain(po);
                } catch (DuplicateKeyException e) {
                    // 并发入库撞上 uk_stock：别的请求已抢先建了这条库存，退化为累加
                    return accumulate(selectByUniqueKey(inbound), inbound.getQuantity());
                }
            }
            // 已有库存：只累加结存，生产日期 / 有效期保持原记录不动
            return accumulate(existing, inbound.getQuantity());
        });
    }

    @Override
    public Mono<PageResult<AmmoStock>> page(int pageNum, int pageSize, Long siteId, String ammoType) {
        return this.<PageResult<AmmoStock>>blocking(() -> {
            try {
                PageHelper.startPage(pageNum, pageSize);
                LambdaQueryWrapper<AmmoStockPO> wrapper = Wrappers.<AmmoStockPO>lambdaQuery();
                if (siteId != null) {
                    wrapper.eq(AmmoStockPO::getSiteId, siteId);
                }
                if (ammoType != null && !ammoType.isBlank()) {
                    wrapper.eq(AmmoStockPO::getAmmoType, ammoType.trim());
                }
                wrapper.orderByAsc(AmmoStockPO::getSiteId)
                        .orderByAsc(AmmoStockPO::getAmmoType)
                        .orderByAsc(AmmoStockPO::getBatchNo);
                List<AmmoStockPO> rows = ammoStockMapper.selectList(wrapper);
                long total = rows instanceof com.github.pagehelper.Page
                        ? ((com.github.pagehelper.Page<?>) rows).getTotal()
                        : rows.size();
                List<AmmoStock> content = rows.stream()
                        .map(AmmoStockPoConverter::toDomain)
                        .collect(Collectors.toList());
                return new PageResult<>(content, total, pageNum, pageSize);
            } finally {
                PageHelper.clearPage();
            }
        });
    }

    /** 按「作业点 + 弹型 + 批次」查唯一库存记录（@TableLogic 自动过滤已删除）。 */
    private AmmoStockPO selectByUniqueKey(AmmoStock inbound) {
        return ammoStockMapper.selectOne(Wrappers.<AmmoStockPO>lambdaQuery()
                .eq(AmmoStockPO::getSiteId, inbound.getSiteId())
                .eq(AmmoStockPO::getAmmoType, inbound.getAmmoType())
                .eq(AmmoStockPO::getBatchNo, inbound.getBatchNo()));
    }

    /**
     * 累加结存并返回最新库存。
     * 用 setSql 让数据库侧做 quantity = quantity + n 的原子加，避免并发入库时的读-改-写丢更新；
     * delta 是领域层校验过的正整数，拼接无注入风险。
     */
    private AmmoStock accumulate(AmmoStockPO existing, int delta) {
        LambdaUpdateWrapper<AmmoStockPO> update = Wrappers.<AmmoStockPO>lambdaUpdate()
                .eq(AmmoStockPO::getId, existing.getId())
                .setSql("quantity = quantity + " + delta);
        ammoStockMapper.update(null, update);
        return AmmoStockPoConverter.toDomain(ammoStockMapper.selectById(existing.getId()));
    }

    /**
     * 阻塞 DB 调用 → 响应式链路的桥接器。
     * 先 deferContextual 取 Reactor Context 里的操作人，再 subscribeOn 切线程，顺序不能颠倒。
     */
    private <T> Mono<T> blocking(Supplier<T> supplier) {
        return Mono.deferContextual(ctx -> {
            String operator = ReactiveOperatorContext.getOperator(ctx);
            return Mono.fromCallable(() -> {
                AuditContextHolder.setOperator(operator);
                try {
                    return supplier.get();
                } finally {
                    AuditContextHolder.clear();
                }
            }).subscribeOn(Schedulers.boundedElastic());
        });
    }
}
