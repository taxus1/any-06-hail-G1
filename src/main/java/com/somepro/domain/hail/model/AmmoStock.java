package com.somepro.domain.hail.model;

import com.somepro.common.exception.BizException;
import com.somepro.domain.shared.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 弹药库存聚合根（hail 限界上下文）。
 *
 * 纯领域对象：只描述业务与不变量，不带任何持久化注解（表映射在基础设施层的 AmmoStockPO）。
 *
 * 库存按「作业点 + 弹型 + 批次」唯一（数据库 uk_stock 兜底）：同一组合的再次入库
 * 不另起记录，而是累加到原有结存上（见 AmmoStockRepositoryImpl#inbound）。
 */
@Getter
@Setter
public class AmmoStock extends BaseEntity {

    private Long id;

    /** 作业点 id（t_operation_site.id） */
    private Long siteId;

    /** 弹型（如 BL-1A） */
    private String ammoType;

    /** 批次号 */
    private String batchNo;

    /** 结存发数（入库场景下也用作「本次入库发数」的载体） */
    private Integer quantity;

    /** 生产日期 */
    private LocalDate produceDate;

    /** 有效期至（含当日） */
    private LocalDate expireDate;

    /**
     * 工厂方法：构造一次入库（新批次即新库存，已有批次则取其 quantity 累加）。
     * 保证初始不变量：作业点、弹型、批次必填，入库发数为正，有效期不早于生产日期。
     */
    public static AmmoStock forInbound(Long siteId, String ammoType, String batchNo, Integer quantity,
                                       LocalDate produceDate, LocalDate expireDate) {
        AmmoStock stock = new AmmoStock();
        if (siteId == null) {
            throw new BizException("入库必须指定作业点，siteId 不能为空");
        }
        stock.setSiteId(siteId);
        stock.setAmmoType(requireText(ammoType, "弹型不能为空"));
        stock.setBatchNo(requireText(batchNo, "批次号不能为空"));
        if (quantity == null || quantity <= 0) {
            throw new BizException("入库数量必须为正整数");
        }
        stock.setQuantity(quantity);
        if (produceDate != null && expireDate != null && expireDate.isBefore(produceDate)) {
            throw new BizException("有效期至不能早于生产日期");
        }
        stock.setProduceDate(produceDate);
        stock.setExpireDate(expireDate);
        return stock;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BizException(message);
        }
        return value.trim();
    }
}
