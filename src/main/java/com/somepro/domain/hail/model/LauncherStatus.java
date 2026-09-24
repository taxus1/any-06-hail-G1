package com.somepro.domain.hail.model;

import com.somepro.common.exception.BizException;

/**
 * 发射装备状态（领域枚举，纯领域、无框架注解）。
 *
 * READY 待命 / IN_USE 作业中 / MAINTENANCE 检修 / RETIRED 退役，落库时存 name() 字符串。
 */
public enum LauncherStatus {

    /** 待命 */
    READY,
    /** 作业中 */
    IN_USE,
    /** 检修 */
    MAINTENANCE,
    /** 退役 */
    RETIRED;

    /** 按编码解析，非法值抛业务异常而不是让框架转换错误裸奔。 */
    public static LauncherStatus of(String code) {
        if (code == null || code.isBlank()) {
            throw new BizException("装备状态不能为空，可选：READY/IN_USE/MAINTENANCE/RETIRED");
        }
        try {
            return LauncherStatus.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BizException("非法装备状态：" + code + "，可选：READY/IN_USE/MAINTENANCE/RETIRED");
        }
    }
}
