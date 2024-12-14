package com.lhm.lhmpicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.lhm.lhmpicturebackend.config.CosClientConfig;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
/**
 * 文件管理服务类，提供文件上传等功能
 */
public class FileManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片方法
     *
     * @param multipartFile    要上传的图片文件
     * @param uploadPathPrefix 图片上传路径的前缀
     * @return 返回上传图片的结果信息
     * @throws BusinessException 当上传过程中发生错误时抛出
     */
    public  UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 验证图片是否有效
        validPicture(multipartFile);

        // 生成一个长度为16的随机字符串作为唯一标识
        String uuid = RandomUtil.randomString(16);

        // 获取原始文件名
        String originFilename = multipartFile.getOriginalFilename();

        // 格式化上传文件名，包含日期、随机字符串和文件后缀
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));

        // 拼接上传路径和文件名
        String uploadPath = String.format("%s/%s", uploadPathPrefix, uploadFilename);

        // 创建一个临时文件
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 将上传的文件转移到临时文件中
            multipartFile.transferTo(file);

            // 将文件上传到COS（云对象存储）
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);

            // 获取上传结果中的图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            // 创建上传图片结果对象
            UploadPictureResult uploadPictureResult = new UploadPictureResult();

            // 获取并计算图片的宽度、高度和比例
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            // 设置上传图片结果的信息
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);

            // 返回上传图片的结果
            return uploadPictureResult;
        } catch (IOException e) {
            // 记录文件上传失败的日志
            log.error("文件上传失败", e);
            // 抛出业务异常，表示上传失败
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 删除临时文件
            this.deleteTempFile(file);
        }
    }
        /**
         * 校验上传的图片是否符合规范
         *
         * @param multipartFile 要校验的图片文件
         * @throws BusinessException 当图片不符合规范时抛出
         */
        public void validPicture (MultipartFile multipartFile){
            ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
            long fileSize = multipartFile.getSize();
            final long ONE_M = 1024 * 1024L;
            ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2M");
            String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
            final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
            ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
        }

        /**
         * 删除临时文件
         *
         * @param file 要删除的临时文件
         */
        public void deleteTempFile (File file){
            if (file == null) {
                return;
            }
            // 删除临时文件
            boolean deleteResult = file.delete();
            if (!deleteResult) {
                log.error("file delete error, filepath = {}", file.getAbsolutePath());
            }
        }
    }

