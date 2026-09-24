package com.somepro.interfaces.rest.hail.converter;

import com.somepro.domain.hail.model.AmmoStock;
import com.somepro.domain.shared.model.PageResult;
import com.somepro.interfaces.rest.common.vo.PageVO;
import com.somepro.interfaces.rest.hail.vo.AmmoStockVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AmmoStock（领域）→ AmmoStockVO（对外）转换器（用户接口层）。
 *
 * 接口层是唯一做领域对象 → VO 转换的地方：Controller 不许直接把领域对象塞进 Result 返回。
 */
public final class AmmoStockVoConverter {

    private AmmoStockVoConverter() {
    }

    public static AmmoStockVO toVo(AmmoStock domain) {
        return new AmmoStockVO(domain.getId(), domain.getSiteId(), domain.getAmmoType(),
                domain.getBatchNo(), domain.getQuantity(), domain.getProduceDate(), domain.getExpireDate(),
                domain.getCreateTime());
    }

    public static PageVO<AmmoStockVO> toPageVo(PageResult<AmmoStock> page) {
        List<AmmoStockVO> content = page.content().stream()
                .map(AmmoStockVoConverter::toVo)
                .collect(Collectors.toList());
        return new PageVO<>(content, page.total(), page.pageNum(), page.pageSize(), page.totalPages());
    }
}
