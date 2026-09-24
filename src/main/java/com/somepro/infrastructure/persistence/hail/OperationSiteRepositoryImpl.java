package com.somepro.infrastructure.persistence.hail;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.somepro.common.exception.BizException;
import com.somepro.domain.hail.model.OperationSite;
import com.somepro.domain.hail.repository.OperationSiteRepository;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.infrastructure.config.ReactiveOperatorContext;
import com.somepro.infrastructure.persistence.audit.AuditContextHolder;
import com.somepro.infrastructure.persistence.hail.converter.OperationSitePoConverter;
import com.somepro.infrastructure.persistence.hail.po.OperationSitePO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 作业点档案仓储适配器：用 MyBatis-Plus 实现领域仓储端口（基础设施层）。
 *
 * 约定同 demo 模块：所有 DB 调用经 {@link #blocking} 桥接到 boundedElastic；
 * PO 与领域对象在本类里经 OperationSitePoConverter 互转，不外泄。
 */
@Repository
public class OperationSiteRepositoryImpl implements OperationSiteRepository {

    private final OperationSiteMapper operationSiteMapper;

    public OperationSiteRepositoryImpl(OperationSiteMapper operationSiteMapper) {
        this.operationSiteMapper = operationSiteMapper;
    }

    @Override
    public Mono<OperationSite> save(OperationSite site) {
        return blocking(() -> {
            OperationSitePO po = OperationSitePoConverter.toPo(site);
            if (po.getId() == null) {
                po.setId(IdUtil.getSnowflakeNextId());
                try {
                    operationSiteMapper.insert(po);
                } catch (DuplicateKeyException e) {
                    // 应用层已查重，这里是并发下唯一键 uk_site_code 的兜底
                    throw new BizException("作业点编号已存在：" + po.getSiteCode());
                }
            } else {
                operationSiteMapper.updateById(po);
            }
            return OperationSitePoConverter.toDomain(po);
        });
    }

    @Override
    public Mono<OperationSite> findById(Long id) {
        return blocking(() -> {
            OperationSitePO po = operationSiteMapper.selectById(id);
            return po == null ? null : OperationSitePoConverter.toDomain(po);
        });
    }

    @Override
    public Mono<Boolean> existsByCode(String siteCode) {
        return blocking(() -> operationSiteMapper.exists(Wrappers.<OperationSitePO>lambdaQuery()
                .eq(OperationSitePO::getSiteCode, siteCode)));
    }

    @Override
    public Mono<PageResult<OperationSite>> page(int pageNum, int pageSize, String keyword) {
        return this.<PageResult<OperationSite>>blocking(() -> {
            try {
                PageHelper.startPage(pageNum, pageSize);
                LambdaQueryWrapper<OperationSitePO> wrapper = Wrappers.<OperationSitePO>lambdaQuery();
                if (keyword != null && !keyword.isBlank()) {
                    String kw = keyword.trim();
                    // 名称或编号任一命中即可
                    wrapper.and(w -> w.like(OperationSitePO::getSiteName, kw)
                            .or()
                            .like(OperationSitePO::getSiteCode, kw));
                }
                wrapper.orderByAsc(OperationSitePO::getSiteCode);
                List<OperationSitePO> rows = operationSiteMapper.selectList(wrapper);
                long total = rows instanceof com.github.pagehelper.Page
                        ? ((com.github.pagehelper.Page<?>) rows).getTotal()
                        : rows.size();
                List<OperationSite> content = rows.stream()
                        .map(OperationSitePoConverter::toDomain)
                        .collect(Collectors.toList());
                return new PageResult<>(content, total, pageNum, pageSize);
            } finally {
                PageHelper.clearPage();
            }
        });
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
