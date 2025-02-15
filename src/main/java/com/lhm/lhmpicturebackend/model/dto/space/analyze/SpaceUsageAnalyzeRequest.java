package com.lhm.lhmpicturebackend.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 空间使用情况分析请求类
 * 继承自 SpaceAnalyzeRequest，用于空间使用情况的分析
 * 主要用于分析空间使用的详细信息，如使用率、剩余空间等
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUsageAnalyzeRequest extends SpaceAnalyzeRequest {

}
