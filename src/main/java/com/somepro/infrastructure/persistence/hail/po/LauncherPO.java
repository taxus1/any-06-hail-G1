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
 * t_launcher 表的持久化对象（PO，基础设施层）。
 *
 * 只描述「表长什么样」：字段与列一一对应，不放任何业务规则（规则在领域对象 Launcher）。
 * 状态列存枚举 name() 字符串，与领域枚举 LauncherStatus 的互转见 LauncherPoConverter。
 *
 * ID 策略 IdType.INPUT：由应用层用雪花算法分配后传入。
 */
@Getter
@Setter
@TableName("t_launcher")
public class LauncherPO extends BasePO {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("launcher_code")
    private String launcherCode;

    @TableField("site_id")
    private Long siteId;

    @TableField("model")
    private String model;

    @TableField("barrel_count")
    private Integer barrelCount;

    @TableField("status")
    private String status;

    @TableField("check_date")
    private LocalDate checkDate;
}
