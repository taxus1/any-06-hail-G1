package com.somepro.infrastructure.persistence.hail.converter;

import com.somepro.domain.hail.model.OperationSite;
import com.somepro.domain.hail.model.SiteStatus;
import com.somepro.infrastructure.persistence.hail.po.OperationSitePO;

/**
 * OperationSitePO（表）↔ OperationSite（领域）转换器（基础设施层）。
 *
 * 状态在库里存枚举 name() 字符串，这里负责 String ↔ SiteStatus 的互转。
 * 审计字段与 delFlag 一并搬运，同 demo 模块约定。
 */
public final class OperationSitePoConverter {

    private OperationSitePoConverter() {
    }

    public static OperationSitePO toPo(OperationSite domain) {
        OperationSitePO po = new OperationSitePO();
        po.setId(domain.getId());
        po.setSiteCode(domain.getSiteCode());
        po.setSiteName(domain.getSiteName());
        po.setCounty(domain.getCounty());
        po.setAltitudeM(domain.getAltitudeM());
        po.setContactName(domain.getContactName());
        po.setContactPhone(domain.getContactPhone());
        po.setStatus(domain.getStatus() == null ? null : domain.getStatus().name());
        po.setDelFlag(domain.getDelFlag());
        po.setCreateBy(domain.getCreateBy());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateBy(domain.getUpdateBy());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }

    public static OperationSite toDomain(OperationSitePO po) {
        OperationSite domain = new OperationSite();
        domain.setId(po.getId());
        domain.setSiteCode(po.getSiteCode());
        domain.setSiteName(po.getSiteName());
        domain.setCounty(po.getCounty());
        domain.setAltitudeM(po.getAltitudeM());
        domain.setContactName(po.getContactName());
        domain.setContactPhone(po.getContactPhone());
        domain.setStatus(po.getStatus() == null ? null : SiteStatus.of(po.getStatus()));
        domain.setDelFlag(po.getDelFlag());
        domain.setCreateBy(po.getCreateBy());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdateBy(po.getUpdateBy());
        domain.setUpdateTime(po.getUpdateTime());
        return domain;
    }
}
