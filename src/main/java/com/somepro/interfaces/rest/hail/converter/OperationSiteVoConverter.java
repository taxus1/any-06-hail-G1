package com.somepro.interfaces.rest.hail.converter;

import com.somepro.domain.hail.model.OperationSite;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.vo.OperationSiteVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OperationSite（领域）→ OperationSiteVO（对外）转换器（用户接口层）。
 *
 * 接口层是唯一做领域对象 → VO 转换的地方：Controller 不许直接把领域对象塞进 Result 返回。
 */
public final class OperationSiteVoConverter {

    private OperationSiteVoConverter() {
    }

    public static OperationSiteVO toVo(OperationSite domain) {
        return new OperationSiteVO(domain.getId(), domain.getSiteCode(), domain.getSiteName(),
                domain.getCounty(), domain.getAltitudeM(), domain.getContactName(), domain.getContactPhone(),
                domain.getStatus() == null ? null : domain.getStatus().name(),
                domain.getCreateTime());
    }

    public static PageVO<OperationSiteVO> toPageVo(PageResult<OperationSite> page) {
        List<OperationSiteVO> content = page.content().stream()
                .map(OperationSiteVoConverter::toVo)
                .collect(Collectors.toList());
        return new PageVO<>(content, page.total(), page.pageNum(), page.pageSize(), page.totalPages());
    }
}
