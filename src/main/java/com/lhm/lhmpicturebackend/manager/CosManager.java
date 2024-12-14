package com.lhm.lhmpicturebackend.manager;

import com.lhm.lhmpicturebackend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
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
     * 上传对象（附带图片信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);

    }
}
