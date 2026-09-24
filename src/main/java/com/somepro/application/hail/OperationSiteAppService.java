package com.somepro.application.hail;

import com.somepro.common.exception.BizException;
import com.somepro.domain.hail.model.OperationSite;
import com.somepro.domain.hail.model.SiteStatus;
import com.somepro.domain.hail.repository.OperationSiteRepository;
import com.somepro.domain.shared.model.PageResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 作业点档案应用服务：编排「登记点位、分页翻看」用例，业务规则在领域层。
 *
 * 出入参都是领域对象（OperationSite），不认识 PO、也不认识 VO。
 */
@Service
public class OperationSiteAppService {

    private final OperationSiteRepository operationSiteRepository;

    public OperationSiteAppService(OperationSiteRepository operationSiteRepository) {
        this.operationSiteRepository = operationSiteRepository;
    }

    /** 登记作业点：编号查重后落库（唯一键 uk_site_code 兜底并发）。 */
    public Mono<OperationSite> registerSite(String siteCode, String siteName, String county, Integer altitudeM,
                                            String contactName, String contactPhone, String status) {
        OperationSite site = OperationSite.register(siteCode, siteName, county, altitudeM,
                contactName, contactPhone, SiteStatus.of(status));
        return operationSiteRepository.existsByCode(site.getSiteCode())
                .flatMap(exists -> exists
                        ? Mono.error(new BizException("作业点编号已存在：" + site.getSiteCode()))
                        : operationSiteRepository.save(site));
    }

    /** 分页翻看；keyword 非空时按名称或编号模糊匹配。 */
    public Mono<PageResult<OperationSite>> pageSites(int pageNum, int pageSize, String keyword) {
        return operationSiteRepository.page(pageNum, pageSize, keyword);
    }
}
