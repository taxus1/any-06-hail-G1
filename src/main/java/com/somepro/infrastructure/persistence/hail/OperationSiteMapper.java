package com.somepro.infrastructure.persistence.hail;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.somepro.infrastructure.persistence.hail.po.OperationSitePO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 作业点档案的 MyBatis-Plus Mapper（基础设施层）。
 *
 * 这是阻塞（JDBC）API，只能在 boundedElastic 线程上调用（见 OperationSiteRepositoryImpl#blocking）。
 */
@Mapper
public interface OperationSiteMapper extends BaseMapper<OperationSitePO> {
}
