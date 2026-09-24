package com.somepro.domain.hail.repository;

import com.somepro.domain.hail.model.OperationSite;
import com.somepro.domain.shared.model.PageResult;
import reactor.core.publisher.Mono;

/**
 * 作业点档案的仓储端口：由领域层定义，基础设施层实现（端口-适配器）。
 */
public interface OperationSiteRepository {

    Mono<OperationSite> save(OperationSite site);

    Mono<OperationSite> findById(Long id);

    /** 编号是否已存在（登记前查重；数据库 uk_site_code 兜底）。 */
    Mono<Boolean> existsByCode(String siteCode);

    /** 分页翻看；keyword 非空时按名称或编号模糊匹配。 */
    Mono<PageResult<OperationSite>> page(int pageNum, int pageSize, String keyword);
}
