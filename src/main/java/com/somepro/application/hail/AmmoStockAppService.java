package com.somepro.application.hail;

import com.somepro.common.exception.BizException;
import com.somepro.domain.hail.model.AmmoStock;
import com.somepro.domain.hail.repository.AmmoStockRepository;
import com.somepro.domain.hail.repository.OperationSiteRepository;
import com.somepro.domain.shared.model.PageResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * 弹药库存应用服务：编排「弹药入库、分页翻看」用例，业务规则在领域层。
 *
 * 出入参都是领域对象（AmmoStock），不认识 PO、也不认识 VO。
 */
@Service
public class AmmoStockAppService {

    private final AmmoStockRepository ammoStockRepository;
    private final OperationSiteRepository operationSiteRepository;

    public AmmoStockAppService(AmmoStockRepository ammoStockRepository,
                               OperationSiteRepository operationSiteRepository) {
        this.ammoStockRepository = ammoStockRepository;
        this.operationSiteRepository = operationSiteRepository;
    }

    /**
     * 弹药入库：作业点必须真实存在；同一「作业点 + 弹型 + 批次」重复入库时
     * 不另起记录，累加到原有结存上（由仓储适配器保证）。
     */
    public Mono<AmmoStock> inbound(Long siteId, String ammoType, String batchNo, Integer quantity,
                                   LocalDate produceDate, LocalDate expireDate) {
        AmmoStock inbound = AmmoStock.forInbound(siteId, ammoType, batchNo, quantity, produceDate, expireDate);
        return operationSiteRepository.findById(inbound.getSiteId())
                .switchIfEmpty(Mono.error(new BizException("作业点不存在，id=" + inbound.getSiteId())))
                .flatMap(site -> ammoStockRepository.inbound(inbound));
    }

    /** 分页翻看；siteId / ammoType 非空时按条件过滤。 */
    public Mono<PageResult<AmmoStock>> pageStocks(int pageNum, int pageSize, Long siteId, String ammoType) {
        return ammoStockRepository.page(pageNum, pageSize, siteId, ammoType);
    }
}
