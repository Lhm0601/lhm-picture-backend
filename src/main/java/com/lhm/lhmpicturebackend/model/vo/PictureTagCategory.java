package com.lhm.lhmpicturebackend.model.vo;

import lombok.Data;

import java.util.List;
// PictureTagCategory类用于封装图片的标签和分类信息
@Data
public class PictureTagCategory {

    // tagList存储图片的标签列表
    private List<String> tagList;

    // categoryList存储图片的分类列表
    private List<String> categoryList;
}
