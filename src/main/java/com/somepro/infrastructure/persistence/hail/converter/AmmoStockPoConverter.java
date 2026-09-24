package com.somepro.infrastructure.persistence.hail.converter;

import com.somepro.domain.hail.model.AmmoStock;
import com.somepro.infrastructure.persistence.hail.po.AmmoStockPO;

/**
 * AmmoStockPO（表）↔ AmmoStock（领域）转换器（基础设施层）。
 *
 * 审计字段与 delFlag 一并搬运，同 demo 模块约定。
 */
public final class AmmoStockPoConverter {

    private AmmoStockPoConverter() {
    }

    public static AmmoStockPO toPo(AmmoStock domain) {
        AmmoStockPO po = new AmmoStockPO();
        po.setId(domain.getId());
        po.setSiteId(domain.getSiteId());
        po.setAmmoType(domain.getAmmoType());
        po.setBatchNo(domain.getBatchNo());
        po.setQuantity(domain.getQuantity());
        po.setProduceDate(domain.getProduceDate());
        po.setExpireDate(domain.getExpireDate());
        po.setDelFlag(domain.getDelFlag());
        po.setCreateBy(domain.getCreateBy());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateBy(domain.getUpdateBy());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }

    public static AmmoStock toDomain(AmmoStockPO po) {
        AmmoStock domain = new AmmoStock();
        domain.setId(po.getId());
        domain.setSiteId(po.getSiteId());
        domain.setAmmoType(po.getAmmoType());
        domain.setBatchNo(po.getBatchNo());
        domain.setQuantity(po.getQuantity());
        domain.setProduceDate(po.getProduceDate());
        domain.setExpireDate(po.getExpireDate());
        domain.setDelFlag(po.getDelFlag());
        domain.setCreateBy(po.getCreateBy());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdateBy(po.getUpdateBy());
        domain.setUpdateTime(po.getUpdateTime());
        return domain;
    }
}
