package com.somepro.infrastructure.persistence.hail;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.somepro.common.exception.BizException;
import com.somepro.domain.hail.model.Launcher;
import com.somepro.domain.hail.repository.LauncherRepository;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.infrastructure.config.ReactiveOperatorContext;
import com.somepro.infrastructure.persistence.audit.AuditContextHolder;
import com.somepro.infrastructure.persistence.hail.converter.LauncherPoConverter;
import com.somepro.infrastructure.persistence.hail.po.LauncherPO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 发射装备台账仓储适配器：用 MyBatis-Plus 实现领域仓储端口（基础设施层）。
 *
 * 约定同 demo 模块：所有 DB 调用经 {@link #blocking} 桥接到 boundedElastic；
 * PO 与领域对象在本类里经 LauncherPoConverter 互转，不外泄。
 */
@Repository
public class LauncherRepositoryImpl implements LauncherRepository {

    private final LauncherMapper launcherMapper;

    public LauncherRepositoryImpl(LauncherMapper launcherMapper) {
        this.launcherMapper = launcherMapper;
    }

    @Override
    public Mono<Launcher> save(Launcher launcher) {
        return blocking(() -> {
            LauncherPO po = LauncherPoConverter.toPo(launcher);
            if (po.getId() == null) {
                po.setId(IdUtil.getSnowflakeNextId());
                try {
                    launcherMapper.insert(po);
                } catch (DuplicateKeyException e) {
                    // 应用层已查重，这里是并发下唯一键 uk_launcher_code 的兜底
                    throw new BizException("装备编号已存在：" + po.getLauncherCode());
                }
            } else {
                launcherMapper.updateById(po);
            }
            return LauncherPoConverter.toDomain(po);
        });
    }

    @Override
    public Mono<Launcher> findById(Long id) {
        return blocking(() -> {
            LauncherPO po = launcherMapper.selectById(id);
            return po == null ? null : LauncherPoConverter.toDomain(po);
        });
    }

    @Override
    public Mono<Boolean> existsByCode(String launcherCode) {
        return blocking(() -> launcherMapper.exists(Wrappers.<LauncherPO>lambdaQuery()
                .eq(LauncherPO::getLauncherCode, launcherCode)));
    }

    @Override
    public Mono<PageResult<Launcher>> page(int pageNum, int pageSize, Long siteId) {
        return this.<PageResult<Launcher>>blocking(() -> {
            try {
                PageHelper.startPage(pageNum, pageSize);
                LambdaQueryWrapper<LauncherPO> wrapper = Wrappers.<LauncherPO>lambdaQuery();
                if (siteId != null) {
                    wrapper.eq(LauncherPO::getSiteId, siteId);
                }
                wrapper.orderByAsc(LauncherPO::getLauncherCode);
                List<LauncherPO> rows = launcherMapper.selectList(wrapper);
                long total = rows instanceof com.github.pagehelper.Page
                        ? ((com.github.pagehelper.Page<?>) rows).getTotal()
                        : rows.size();
                List<Launcher> content = rows.stream()
                        .map(LauncherPoConverter::toDomain)
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
