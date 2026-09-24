package com.somepro.domain.hail.model;

import com.somepro.common.exception.BizException;

/**
 * 作业点状态（领域枚举，纯领域、无框架注解）。
 *
 * ACTIVE 在册 / SUSPENDED 封存 / CLOSED 撤销，落库时存 name() 字符串（见 PO 转换器）。
 */
public enum SiteStatus {

    /** 在册 */
    ACTIVE,
    /** 封存 */
    SUSPENDED,
    /** 撤销 */
    CLOSED;

    /** 按编码解析，非法值抛业务异常而不是让框架转换错误裸奔。 */
    public static SiteStatus of(String code) {
        if (code == null || code.isBlank()) {
            throw new BizException("作业点状态不能为空，可选：ACTIVE/SUSPENDED/CLOSED");
        }
        try {
            return SiteStatus.valueOf(code.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BizException("非法作业点状态：" + code + "，可选：ACTIVE/SUSPENDED/CLOSED");
        }
    }
}
