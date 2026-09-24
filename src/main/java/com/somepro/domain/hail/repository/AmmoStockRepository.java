package com.somepro.domain.hail.repository;

import com.somepro.domain.hail.model.AmmoStock;
import com.somepro.domain.shared.model.PageResult;
import reactor.core.publisher.Mono;

/**
 * 弹药库存的仓储端口：由领域层定义，基础设施层实现（端口-适配器）。
 */
public interface AmmoStockRepository {

    /**
     * 入库：按「作业点 + 弹型 + 批次」查找，已存在则把 quantity 累加到原结存上，
     * 不存在则新起一条库存记录。返回入库后的最新库存。
     */
    Mono<AmmoStock> inbound(AmmoStock inbound);

    /** 分页翻看；siteId / ammoType 非空时按条件过滤。 */
    Mono<PageResult<AmmoStock>> page(int pageNum, int pageSize, Long siteId, String ammoType);
}
