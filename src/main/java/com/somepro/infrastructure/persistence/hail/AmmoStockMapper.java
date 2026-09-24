package com.somepro.infrastructure.persistence.hail;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.somepro.infrastructure.persistence.hail.po.AmmoStockPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 弹药库存的 MyBatis-Plus Mapper（基础设施层）。
 *
 * 这是阻塞（JDBC）API，只能在 boundedElastic 线程上调用（见 AmmoStockRepositoryImpl#blocking）。
 */
@Mapper
public interface AmmoStockMapper extends BaseMapper<AmmoStockPO> {
}
