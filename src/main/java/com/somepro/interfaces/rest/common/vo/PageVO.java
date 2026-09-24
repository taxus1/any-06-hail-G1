package com.somepro.interfaces.rest.common.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 对外分页返回对象（VO，用户接口层，跨模块共用）—— 不可变 record。
 *
 * 与领域层 PageResult 的分工：PageResult 保持零框架依赖，派生字段 totalPages 放在这里，
 * 方便前端直接渲染分页器。（demo 模块下那份 PageVO 是示例，可删；正式模块统一用这份。）
 */
public record PageVO<T>(List<T> content, long total, int pageNum, int pageSize, int totalPages)
        implements Serializable {
}
