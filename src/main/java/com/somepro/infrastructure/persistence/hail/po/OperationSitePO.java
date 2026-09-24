package com.somepro.infrastructure.persistence.hail.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.somepro.infrastructure.persistence.base.BasePO;
import lombok.Getter;
import lombok.Setter;

/**
 * t_operation_site 表的持久化对象（PO，基础设施层）。
 *
 * 只描述「表长什么样」：字段与列一一对应，不放任何业务规则（规则在领域对象 OperationSite）。
 * 状态列存枚举 name() 字符串，与领域枚举 SiteStatus 的互转见 OperationSitePoConverter。
 *
 * ID 策略 IdType.INPUT：由应用层用雪花算法分配后传入。
 */
@Getter
@Setter
@TableName("t_operation_site")
public class OperationSitePO extends BasePO {

    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @TableField("site_code")
    private String siteCode;

    @TableField("site_name")
    private String siteName;

    @TableField("county")
    private String county;

    @TableField("altitude_m")
    private Integer altitudeM;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("status")
    private String status;
}
