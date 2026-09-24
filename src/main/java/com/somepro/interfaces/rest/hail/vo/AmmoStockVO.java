package com.somepro.interfaces.rest.hail.vo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 弹药库存对外返回对象（VO，用户接口层）—— 不可变 record。
 *
 * 只暴露允许外部看到的字段；delFlag / createBy / updateBy / updateTime 留在内部，不进 API 契约。
 */
public record AmmoStockVO(Long id, Long siteId, String ammoType, String batchNo, Integer quantity,
                          LocalDate produceDate, LocalDate expireDate,
                          LocalDateTime createTime) implements Serializable {
}
