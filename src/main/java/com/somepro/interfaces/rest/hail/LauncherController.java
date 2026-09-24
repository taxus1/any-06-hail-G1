package com.somepro.interfaces.rest.hail;

import com.somepro.application.hail.LauncherAppService;
import com.somepro.common.Result;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.converter.LauncherVoConverter;
import com.somepro.interfaces.rest.hail.vo.LauncherVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * 发射装备台账接口（用户接口层）：只做协议适配（参数解析、VO 转换、返回包装），业务编排交给应用层。
 *
 * - POST /api/hail/launcher      登记装备（编号唯一，必须挂在已存在的作业点上，
 *                                状态 READY/IN_USE/MAINTENANCE/RETIRED，缺省 READY）
 * - GET  /api/hail/launcher/list 分页翻看，可按 siteId 过滤
 */
@RestController
@RequestMapping("/api/hail/launcher")
public class LauncherController {

    private final LauncherAppService launcherAppService;

    public LauncherController(LauncherAppService launcherAppService) {
        this.launcherAppService = launcherAppService;
    }

    @PostMapping
    public Mono<Result<LauncherVO>> register(@RequestParam String launcherCode,
                                             @RequestParam Long siteId,
                                             @RequestParam(required = false) String model,
                                             @RequestParam(required = false) Integer barrelCount,
                                             @RequestParam(defaultValue = "READY") String status,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkDate) {
        return launcherAppService.registerLauncher(launcherCode, siteId, model, barrelCount, status, checkDate)
                .map(LauncherVoConverter::toVo)
                .map(Result::ok);
    }

    @GetMapping("/list")
    public Mono<Result<PageVO<LauncherVO>>> list(@RequestParam(defaultValue = "1") int pageNum,
                                                 @RequestParam(defaultValue = "20") int pageSize,
                                                 @RequestParam(required = false) Long siteId) {
        return launcherAppService.pageLaunchers(pageNum, pageSize, siteId)
                .map(LauncherVoConverter::toPageVo)
                .map(Result::ok);
    }
}
