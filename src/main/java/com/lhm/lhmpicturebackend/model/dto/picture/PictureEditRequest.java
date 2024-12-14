package com.lhm.lhmpicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片编辑请求类
 * 用于序列化和反序列化图片编辑相关的请求数据
 */
@Data
public class PictureEditRequest implements Serializable {

    /**
     * 图片的唯一标识符
     * 用于标识和定位数据库中的图片对象
     */
    private Long id;

    /**
     * 图片名称
     * 描述图片的内容或主题
     */
    private String name;

    /**
     * 图片的简介信息
     * 提供图片的详细描述或背景信息
     */
    private String introduction;

    /**
     * 图片的分类
     * 用于对图片进行分类管理
     */
    private String category;

    /**
     * 图片的标签列表
     * 用于标记图片的关键字或主题，便于搜索和管理
     */
    private List<String> tags;

    /**
     * 序列化版本唯一标识符
     * 确保类的版本在序列化过程中保持一致
     */
    private static final long serialVersionUID = 1L;
}

