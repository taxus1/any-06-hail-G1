package com.somepro.interfaces.rest.hail;

import com.somepro.application.hail.AmmoStockAppService;
import com.somepro.common.Result;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.converter.AmmoStockVoConverter;
import com.somepro.interfaces.rest.hail.vo.AmmoStockVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * 弹药库存接口（用户接口层）：只做协议适配（参数解析、VO 转换、返回包装），业务编排交给应用层。
 *
 * - POST /api/hail/ammo/inbound 弹药入库（同一作业点 + 弹型 + 批次重复入库时累加结存，不另起记录）
 * - GET  /api/hail/ammo/list    分页翻看，可按 siteId / ammoType 过滤
 */
@RestController
@RequestMapping("/api/hail/ammo")
public class AmmoStockController {

    private final AmmoStockAppService ammoStockAppService;

    public AmmoStockController(AmmoStockAppService ammoStockAppService) {
        this.ammoStockAppService = ammoStockAppService;
    }

    @PostMapping("/inbound")
    public Mono<Result<AmmoStockVO>> inbound(@RequestParam Long siteId,
                                             @RequestParam String ammoType,
                                             @RequestParam String batchNo,
                                             @RequestParam Integer quantity,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate produceDate,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireDate) {
        return ammoStockAppService.inbound(siteId, ammoType, batchNo, quantity, produceDate, expireDate)
                .map(AmmoStockVoConverter::toVo)
                .map(Result::ok);
    }

    @GetMapping("/list")
    public Mono<Result<PageVO<AmmoStockVO>>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "20") int pageSize,
                                                  @RequestParam(required = false) Long siteId,
                                                  @RequestParam(required = false) String ammoType) {
        return ammoStockAppService.pageStocks(pageNum, pageSize, siteId, ammoType)
                .map(AmmoStockVoConverter::toPageVo)
                .map(Result::ok);
    }
}
