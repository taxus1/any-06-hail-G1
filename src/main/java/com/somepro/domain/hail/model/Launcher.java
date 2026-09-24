package com.somepro.domain.hail.model;

import com.somepro.common.exception.BizException;
import com.somepro.domain.shared.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 发射装备台账聚合根（hail 限界上下文）。
 *
 * 纯领域对象：只描述业务与不变量，不带任何持久化注解（表映射在基础设施层的 LauncherPO）。
 *
 * 不变量：
 * - 装备编号（launcherCode，如 ZB-0007）必填，全局唯一由应用层 + 数据库唯一键双重保证；
 * - 每台装备必须归属一个作业点（siteId 必填），「作业点真实存在」由应用层在登记时校验；
 * - 状态缺省为 READY（待命）。
 */
@Getter
@Setter
public class Launcher extends BaseEntity {

    private Long id;

    /** 装备编号，全局唯一（如 ZB-0007） */
    private String launcherCode;

    /** 归属作业点 id（t_operation_site.id） */
    private Long siteId;

    /** 装备型号 */
    private String model;

    /** 发射管数 */
    private Integer barrelCount;

    private LauncherStatus status;

    /** 最近检验日期 */
    private LocalDate checkDate;

    /** 工厂方法：登记装备并保证初始不变量。 */
    public static Launcher register(String launcherCode, Long siteId, String model, Integer barrelCount,
                                    LauncherStatus status, LocalDate checkDate) {
        Launcher launcher = new Launcher();
        launcher.setLauncherCode(requireText(launcherCode, "装备编号不能为空"));
        if (siteId == null) {
            throw new BizException("装备必须归属某个作业点，siteId 不能为空");
        }
        launcher.setSiteId(siteId);
        launcher.setModel(model);
        if (barrelCount != null && barrelCount <= 0) {
            throw new BizException("发射管数必须为正整数");
        }
        launcher.setBarrelCount(barrelCount);
        launcher.setStatus(status == null ? LauncherStatus.READY : status);
        launcher.setCheckDate(checkDate);
        return launcher;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BizException(message);
        }
        return value.trim();
    }
}
