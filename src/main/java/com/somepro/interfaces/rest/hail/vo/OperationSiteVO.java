package com.somepro.interfaces.rest.hail.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作业点档案对外返回对象（VO，用户接口层）—— 不可变 record。
 *
 * 只暴露允许外部看到的字段；delFlag / createBy / updateBy / updateTime 留在内部，不进 API 契约。
 */
public record OperationSiteVO(Long id, String siteCode, String siteName, String county, Integer altitudeM,
                              String contactName, String contactPhone, String status,
                              LocalDateTime createTime) implements Serializable {
}
