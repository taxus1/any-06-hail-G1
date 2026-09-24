package com.somepro.interfaces.rest.hail;

import com.somepro.application.hail.OperationSiteAppService;
import com.somepro.common.Result;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.converter.OperationSiteVoConverter;
import com.somepro.interfaces.rest.hail.vo.OperationSiteVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 作业点档案接口（用户接口层）：只做协议适配（参数解析、VO 转换、返回包装），业务编排交给应用层。
 *
 * - POST /api/hail/site      登记作业点（编号唯一，状态 ACTIVE/SUSPENDED/CLOSED，缺省 ACTIVE）
 * - GET  /api/hail/site/list 分页翻看，keyword 按名称或编号模糊匹配
 */
@RestController
@RequestMapping("/api/hail/site")
public class OperationSiteController {

    private final OperationSiteAppService operationSiteAppService;

    public OperationSiteController(OperationSiteAppService operationSiteAppService) {
        this.operationSiteAppService = operationSiteAppService;
    }

    @PostMapping
    public Mono<Result<OperationSiteVO>> register(@RequestParam String siteCode,
                                                  @RequestParam String siteName,
                                                  @RequestParam(required = false) String county,
                                                  @RequestParam(required = false) Integer altitudeM,
                                                  @RequestParam(required = false) String contactName,
                                                  @RequestParam(required = false) String contactPhone,
                                                  @RequestParam(defaultValue = "ACTIVE") String status) {
        return operationSiteAppService.registerSite(siteCode, siteName, county, altitudeM,
                        contactName, contactPhone, status)
                .map(OperationSiteVoConverter::toVo)
                .map(Result::ok);
    }

    @GetMapping("/list")
    public Mono<Result<PageVO<OperationSiteVO>>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                      @RequestParam(defaultValue = "20") int pageSize,
                                                      @RequestParam(required = false) String keyword) {
        return operationSiteAppService.pageSites(pageNum, pageSize, keyword)
                .map(OperationSiteVoConverter::toPageVo)
                .map(Result::ok);
    }
}
