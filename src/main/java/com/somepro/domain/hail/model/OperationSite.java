package com.somepro.domain.hail.model;

import com.somepro.common.exception.BizException;
import com.somepro.domain.shared.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 作业点档案聚合根（hail 限界上下文）。
 *
 * 纯领域对象：只描述业务与不变量，不带任何持久化注解（表映射在基础设施层的 OperationSitePO）。
 *
 * 不变量：
 * - 作业点编号（siteCode，如 YY-013）与名称必填；编号全局唯一由应用层 + 数据库唯一键双重保证；
 * - 状态缺省为 ACTIVE（在册）。
 */
@Getter
@Setter
public class OperationSite extends BaseEntity {

    private Long id;

    /** 作业点编号，全局唯一（如 YY-013） */
    private String siteCode;

    private String siteName;

    /** 所属县区 */
    private String county;

    /** 海拔（米） */
    private Integer altitudeM;

    /** 值守人姓名 */
    private String contactName;

    /** 值守人电话 */
    private String contactPhone;

    private SiteStatus status;

    /** 工厂方法：登记作业点并保证初始不变量。 */
    public static OperationSite register(String siteCode, String siteName, String county, Integer altitudeM,
                                         String contactName, String contactPhone, SiteStatus status) {
        OperationSite site = new OperationSite();
        site.setSiteCode(requireText(siteCode, "作业点编号不能为空"));
        site.setSiteName(requireText(siteName, "作业点名称不能为空"));
        site.setCounty(county);
        site.setAltitudeM(altitudeM);
        site.setContactName(contactName);
        site.setContactPhone(contactPhone);
        site.setStatus(status == null ? SiteStatus.ACTIVE : status);
        return site;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BizException(message);
        }
        return value.trim();
    }
}
