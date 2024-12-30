package com.lhm.lhmpicturebackend.model.dto.picture;

import com.lhm.lhmpicturebackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 图片查询请求类，用于封装图片查询相关的参数
 * 继承自PageRequest以获取分页功能，实现Serializable接口以支持序列化
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * 图片的唯一标识符
     */
    private Long id;

    /**
     * 图片名称，用于查询时匹配
     */
    private String name;

    /**
     * 图片的简介信息，提供对图片内容的简短描述
     */
    private String introduction;

    /**
     * 图片的分类，用于按类别筛选图片
     */
    private String category;

    /**
     * 图片的标签，支持多对多关系，用于更细致地分类图片
     */
    private List<String> tags;

    /**
     * 图片文件的大小，以字节为单位
     */
    private Long picSize;

    /**
     * 图片的宽度，以像素为单位
     */
    private Integer picWidth;

    /**
     * 图片的高度，以像素为单位
     */
    private Integer picHeight;

    /**
     * 图片的宽高比
     */
    private Double picScale;

    /**
     * 图片的格式，如JPEG、PNG等
     */
    private String picFormat;

    /**
     * 搜索文本，用于在名称、简介等字段中进行模糊搜索
     */
    private String searchText;

    /**
     * 用户的唯一标识符，用于筛选属于特定用户的照片
     */
    private Long userId;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;


    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 是否只查询 spaceId 为 null 的数据
     */
    private boolean nullSpaceId;


    // 序列化ID，用于版本控制
    private static final long serialVersionUID = 1L;
}

