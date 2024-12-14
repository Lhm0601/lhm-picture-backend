package com.lhm.lhmpicturebackend.controller;

import com.lhm.lhmpicturebackend.annotation.AuthCheck;
import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.common.ResultUtils;
import com.lhm.lhmpicturebackend.constant.UserConstant;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.manager.CosManager;
import com.lhm.lhmpicturebackend.service.PictureService;
import com.lhm.lhmpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * 文件控制器类，用于处理文件上传等操作
 */
@Slf4j
@RequestMapping("/file")
@RestController
public class FileController {
    // 注入CosManager用于对象存储操作
    @Resource
    private CosManager cosManager;
    @Resource
    private UserService userService;
    @Resource
    PictureService pictureService;

    /**
     * 测试文件上传功能
     * 该方法仅限于管理员角色用户使用，通过上传文件到指定的测试目录，验证文件上传功能
     *
     * @param multipartFile 要上传的文件，类型为MultipartFile
     * @return 返回一个BaseResponse对象，包含上传成功后的文件访问路径
     * @throws BusinessException 当文件上传过程中发生错误时抛出
     */
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN)
    @PostMapping("/test/upload")
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ResultUtils.success(filepath);
        } catch (Exception e) {
            // 记录文件上传错误日志，并抛出业务异常
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    // 记录文件删除错误日志
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

}
