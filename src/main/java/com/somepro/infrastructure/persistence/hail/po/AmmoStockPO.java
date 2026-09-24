package com.somepro.infrastructure.persistence.hail.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.somepro.infrastructure.persistence.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * t_ammo_stock 表的持久化对象（PO，基础设施层）。
 *
 * 只描述「表长什么样」：字段与列一一对应，不放任何业务规则（规则在领域对象 AmmoStock）。
 * 「作业点 + 弹型 + 批次」唯一由表上 uk_stock 保证，重复入库的累加逻辑在仓储适配器。
 *
 * ID 策略 IdType.INPUT：由应用层用雪花算法分配后传入。
 */
@Getter
@Setter
@TableName("t_ammo_stock")
public class AmmoStockPO extends BasePO {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("site_id")
    private Long siteId;

    @TableField("ammo_type")
    private String ammoType;

    @TableField("batch_no")
    private String batchNo;

    @TableField("quantity")
    private Integer quantity;

    @TableField("produce_date")
    private LocalDate produceDate;

    @TableField("expire_date")
    private LocalDate expireDate;
}
