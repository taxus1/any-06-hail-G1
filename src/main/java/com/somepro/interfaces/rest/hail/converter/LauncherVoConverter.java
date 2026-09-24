package com.somepro.interfaces.rest.hail.converter;

import com.somepro.domain.hail.model.Launcher;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.vo.LauncherVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Launcher（领域）→ LauncherVO（对外）转换器（用户接口层）。
 *
 * 接口层是唯一做领域对象 → VO 转换的地方：Controller 不许直接把领域对象塞进 Result 返回。
 */
public final class LauncherVoConverter {

    private LauncherVoConverter() {
    }

    public static LauncherVO toVo(Launcher domain) {
        return new LauncherVO(domain.getId(), domain.getLauncherCode(), domain.getSiteId(),
                domain.getModel(), domain.getBarrelCount(),
                domain.getStatus() == null ? null : domain.getStatus().name(),
                domain.getCheckDate(), domain.getCreateTime());
    }

    public static PageVO<LauncherVO> toPageVo(PageResult<Launcher> page) {
        List<LauncherVO> content = page.content().stream()
                .map(LauncherVoConverter::toVo)
                .collect(Collectors.toList());
        return new PageVO<>(content, page.total(), page.pageNum(), page.pageSize(), page.totalPages());
    }
}
