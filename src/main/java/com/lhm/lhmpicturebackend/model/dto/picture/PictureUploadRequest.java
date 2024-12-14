package com.lhm.lhmpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求类
 * 用于处理图片上传的相关请求
 * 实现了Serializable接口，以确保对象可以被序列化
 */
@Data
public class PictureUploadRequest  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 图片的唯一标识符
     * 用于在系统中唯一标识一张图片
     */
    private Long id;

}
