package com.somepro.infrastructure.persistence.hail.converter;

import com.somepro.domain.hail.model.Launcher;
import com.somepro.domain.hail.model.LauncherStatus;
import com.somepro.infrastructure.persistence.hail.po.LauncherPO;

/**
 * LauncherPO（表）↔ Launcher（领域）转换器（基础设施层）。
 *
 * 状态在库里存枚举 name() 字符串，这里负责 String ↔ LauncherStatus 的互转。
 * 审计字段与 delFlag 一并搬运，同 demo 模块约定。
 */
public final class LauncherPoConverter {

    private LauncherPoConverter() {
    }

    public static LauncherPO toPo(Launcher domain) {
        LauncherPO po = new LauncherPO();
        po.setId(domain.getId());
        po.setLauncherCode(domain.getLauncherCode());
        po.setSiteId(domain.getSiteId());
        po.setModel(domain.getModel());
        po.setBarrelCount(domain.getBarrelCount());
        po.setStatus(domain.getStatus() == null ? null : domain.getStatus().name());
        po.setCheckDate(domain.getCheckDate());
        po.setDelFlag(domain.getDelFlag());
        po.setCreateBy(domain.getCreateBy());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateBy(domain.getUpdateBy());
        po.setUpdateTime(domain.getUpdateTime());
        return po;
    }

    public static Launcher toDomain(LauncherPO po) {
        Launcher domain = new Launcher();
        domain.setId(po.getId());
        domain.setLauncherCode(po.getLauncherCode());
        domain.setSiteId(po.getSiteId());
        domain.setModel(po.getModel());
        domain.setBarrelCount(po.getBarrelCount());
        domain.setStatus(po.getStatus() == null ? null : LauncherStatus.of(po.getStatus()));
        domain.setCheckDate(po.getCheckDate());
        domain.setDelFlag(po.getDelFlag());
        domain.setCreateBy(po.getCreateBy());
        domain.setCreateTime(po.getCreateTime());
        domain.setUpdateBy(po.getUpdateBy());
        domain.setUpdateTime(po.getUpdateTime());
        return domain;
    }
}
