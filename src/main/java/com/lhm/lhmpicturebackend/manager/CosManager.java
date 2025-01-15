package com.lhm.lhmpicturebackend.manager;

import cn.hutool.core.io.FileUtil;
import com.lhm.lhmpicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * CosManager 类用于管理与 COS（Cloud Object Storage）相关的操作
 * 它依赖于 CosClientConfig 类来获取 COS 客户端的配置信息
 * 该类被标记为@Component，表示它是一个Spring组件，可以被自动检测并注册到Spring应用上下文中
 */
@Component
public class CosManager {

    /**
     * 注入 CosClientConfig 对象，用于获取 COS 客户端的配置信息
     */
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 注入 COSClient 对象，用于执行 COS 操作
     */
    @Resource
    private COSClient cosClient;

    // ... 一些操作 COS 的方法

    /**
     * 上传对象到 COS
     *
     * @param key  唯一键，用于在 COS 中唯一标识对象
     * @param file 要上传的文件对象
     * @return 返回上传结果，包含上传成功后的对象信息
     */
    public PutObjectResult putObject(String key, File file) {
        // 创建上传对象请求，指定桶、键和文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 执行上传操作并返回结果
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 向对象存储中上传图片对象，并进行压缩处理
     *
     * @param key  图片对象的键，用于唯一标识对象
     * @param file 要上传的图片文件
     * @return 返回上传后的对象结果
     */
    public PutObjectResult putPictureObject(String key, File file) {
        // 创建上传对象请求，指定桶和对象键以及要上传的文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 创建图片处理规则列表
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩（转成 webp 格式）
//        String webpKey = FileUtil.mainName(key) + ".webp";
//        PicOperations.Rule compressRule = new PicOperations.Rule();
//        compressRule.setRule("imageMogr2/format/webp");
//        compressRule.setBucket(cosClientConfig.getBucket());
//        compressRule.setFileId(webpKey);
//        rules.add(compressRule);
//        // 调整文件大小阈值为 100 KB，增大缩略图宽高为 256x256
//        if (file.length() > 100 * 1024) {
//            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
//            thumbnailRule.setBucket(cosClientConfig.getBucket());
//            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
//            thumbnailRule.setFileId(thumbnailKey);
//
//            // 缩放规则：增大缩略图尺寸为 256x256，保持宽高比，提高质量为 90，去除元数据
//            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>/quality/90/strip", 256, 256));
//
//            rules.add(thumbnailRule);
//        }
        // 图片压缩（转换为 JPEG 格式）
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/jpg"); // 修改为 JPEG 格式
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(FileUtil.mainName(key) + ".jpg"); // 修改文件后缀为 .jpg
        rules.add(compressRule);

        // 调整文件大小阈值为 100 KB，生成缩略图
        if (file.length() > 100 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail.jpg"; // 缩略图格式为 JPEG
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>/quality/90/strip", 256, 256));
            rules.add(thumbnailRule);
        }
        // 构造处理参数
        picOperations.setRules(rules);
        // 设置图片处理操作到上传请求
        putObjectRequest.setPicOperations(picOperations);
        // 执行上传操作并返回结果
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param key 文件 key
     */
    public void deleteObject(String key) throws CosClientException {
        cosClient.deleteObject(cosClientConfig.getBucket(), key);
    }


}

