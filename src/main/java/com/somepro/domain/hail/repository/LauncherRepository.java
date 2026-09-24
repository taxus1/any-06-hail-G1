package com.somepro.domain.hail.repository;

import com.somepro.domain.hail.model.Launcher;
import com.somepro.domain.shared.model.PageResult;
import reactor.core.publisher.Mono;

/**
 * 发射装备台账的仓储端口：由领域层定义，基础设施层实现（端口-适配器）。
 */
public interface LauncherRepository {

    Mono<Launcher> save(Launcher launcher);

    Mono<Launcher> findById(Long id);

    /** 装备编号是否已存在（登记前查重；数据库 uk_launcher_code 兜底）。 */
    Mono<Boolean> existsByCode(String launcherCode);

    /** 分页翻看；siteId 非空时只看某个作业点下的装备。 */
    Mono<PageResult<Launcher>> page(int pageNum, int pageSize, Long siteId);
}
