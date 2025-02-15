package com.lhm.lhmpicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间分析请求类，用于空间分析的请求参数
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

    // 序列化版本号，保证类的版本控制
    private static final long serialVersionUID = 1L;
}
