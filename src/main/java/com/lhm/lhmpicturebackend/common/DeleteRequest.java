package com.lhm.lhmpicturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求类
 * 用于封装删除操作所需的参数
 * 实现了Serializable接口，以支持序列化和反序列化
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     * 存储需要删除的实体的标识符
     */
    private Long id;

    /**
     * 序列化ID
     * 用于标识类的版本，确保类的兼容性
     */
    private static final long serialVersionUID = 1L;
}
