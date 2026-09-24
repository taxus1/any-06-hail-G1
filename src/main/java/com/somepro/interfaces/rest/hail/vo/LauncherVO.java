package com.somepro.interfaces.rest.hail.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 发射装备对外返回对象（VO，用户接口层）—— 不可变 record。
 *
 * 只暴露允许外部看到的字段；delFlag / createBy / updateBy / updateTime 留在内部，不进 API 契约。
 */
public record LauncherVO(Long id, String launcherCode, Long siteId, String model, Integer barrelCount,
                         String status, LocalDate checkDate,
                         LocalDateTime createTime) implements Serializable {
}
