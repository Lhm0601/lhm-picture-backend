package com.lhm.lhmpicturebackend.model.dto.space;

import com.lhm.lhmpicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 空间查询请求类，用于封装空间查询相关的参数
 * 继承自PageRequest以获取分页查询能力，并实现Serializable接口以支持序列化
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;
    /**
     * 空间类型：0-私有 1-团队
     */
    private Integer spaceType;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    // 序列化ID，用于兼容性处理
    private static final long serialVersionUID = 1L;
}
