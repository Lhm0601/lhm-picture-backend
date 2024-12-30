package com.lhm.lhmpicturebackend.model.dto.file;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 上传图片结果类
 * 该类用于存储图片上传后的相关信息，包括图片地址、名称、大小、尺寸、格式等
 */
@Component
@Data
public class UploadPictureResult {

    /**
     * 图片地址
     * 用于存储图片上传后的URL，以便于访问
     */
    private String url;

    /**
     * 图片名称
     * 保存图片的原始名称，便于识别和管理
     */
    private String picName;

    /**
     * 文件体积
     * 记录图片的大小，单位为字节，用于存储空间管理和图片加载策略
     */
    private Long picSize;

    /**
     * 图片宽度
     * 保存图片的宽度，单位为像素，用于界面展示和图片处理
     */
    private int picWidth;

    /**
     * 图片高度
     * 保存图片的高度，单位为像素，与宽度一起用于保持图片的宽高比
     */
    private int picHeight;

    /**
     * 图片宽高比
     * 计算并存储图片的宽高比，用于在不同尺寸的设备上保持图片显示的正确性
     */
    private Double picScale;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;


    /**
     * 图片格式
     * 记录图片的格式，如JPEG、PNG等，用于图片的正确解析和处理
     */
    private String picFormat;

}

