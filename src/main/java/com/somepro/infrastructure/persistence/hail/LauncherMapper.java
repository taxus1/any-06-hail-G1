package com.somepro.infrastructure.persistence.hail;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.somepro.infrastructure.persistence.hail.po.LauncherPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发射装备台账的 MyBatis-Plus Mapper（基础设施层）。
 *
 * 这是阻塞（JDBC）API，只能在 boundedElastic 线程上调用（见 LauncherRepositoryImpl#blocking）。
 */
@Mapper
public interface LauncherMapper extends BaseMapper<LauncherPO> {
}
