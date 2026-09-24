package com.somepro.application.hail;

import com.somepro.common.exception.BizException;
import com.somepro.domain.hail.model.Launcher;
import com.somepro.domain.hail.model.LauncherStatus;
import com.somepro.domain.hail.repository.LauncherRepository;
import com.somepro.domain.hail.repository.OperationSiteRepository;
import com.somepro.domain.shared.model.PageResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 * 发射装备台账应用服务：编排「登记装备、分页翻看」用例，业务规则在领域层。
 *
 * 出入参都是领域对象（Launcher），不认识 PO、也不认识 VO。
 */
@Service
public class LauncherAppService {

    private final LauncherRepository launcherRepository;
    private final OperationSiteRepository operationSiteRepository;

    public LauncherAppService(LauncherRepository launcherRepository,
                              OperationSiteRepository operationSiteRepository) {
        this.launcherRepository = launcherRepository;
        this.operationSiteRepository = operationSiteRepository;
    }

    /** 登记装备：归属作业点必须真实存在（装备不能有没主的），装备编号查重后落库。 */
    public Mono<Launcher> registerLauncher(String launcherCode, Long siteId, String model, Integer barrelCount,
                                           String status, LocalDate checkDate) {
        Launcher launcher = Launcher.register(launcherCode, siteId, model, barrelCount,
                LauncherStatus.of(status), checkDate);
        return operationSiteRepository.findById(launcher.getSiteId())
                .switchIfEmpty(Mono.error(new BizException("归属作业点不存在，id=" + launcher.getSiteId())))
                .flatMap(site -> launcherRepository.existsByCode(launcher.getLauncherCode()))
                .flatMap(exists -> exists
                        ? Mono.error(new BizException("装备编号已存在：" + launcher.getLauncherCode()))
                        : launcherRepository.save(launcher));
    }

    /** 分页翻看；siteId 非空时只看某个作业点下的装备。 */
    public Mono<PageResult<Launcher>> pageLaunchers(int pageNum, int pageSize, Long siteId) {
        return launcherRepository.page(pageNum, pageSize, siteId);
    }
}
