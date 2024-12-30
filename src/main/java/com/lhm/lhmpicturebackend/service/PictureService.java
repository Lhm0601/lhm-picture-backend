package com.lhm.lhmpicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhm.lhmpicturebackend.model.dto.picture.*;
import com.lhm.lhmpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.PictureVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 梁
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2024-12-12 13:58:50
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片方法
     * 该方法负责处理图片上传请求，将图片数据和相关请求信息结合用户登录状态进行处理
     *
     * @param inputSource 前端上传的图片文件，包含图片的二进制数据
     * @param pictureUploadRequest 图片上传请求对象，包含上传图片的相关信息，如描述、标签等
     * @param loginUser 当前登录的用户信息，用于记录图片上传者
     * @return 返回一个PictureVO对象，包含上传图片的信息和访问地址等
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 构建图片查询条件
     * 根据传入的图片查询请求参数构建MyBatis-Plus的QueryWrapper对象，用于数据库查询
     *
     * @param pictureQueryRequest 图片查询请求对象，包含一系列查询条件，如标签、上传者等
     * @return 返回一个QueryWrapper对象，用于执行数据库查询操作
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片VO分页数据
     * 该方法将从数据库中查询到的图片记录转换为图片VO对象，并进行分页处理
     *
     * @param picturePage 图片实体的分页对象，包含从数据库查询到的图片记录
     * @param request HTTP请求对象，用于获取请求上下文信息
     * @return 返回一个PictureVO类型的分页对象，包含转换后的图片信息和分页数据
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 构建单个图片VO对象
     * 根据传入的图片实体和请求对象，构建一个图片VO对象，用于前端展示
     *
     * @param picture 图片实体对象，包含图片的基本信息
     * @param request HTTP请求对象，用于获取请求上下文信息
     * @return 返回一个PictureVO对象，包含图片的详细信息和访问地址等
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 校验图片信息
     * 对传入的图片对象进行合法性校验，确保图片信息符合规范
     *
     * @param picture 图片对象，包含待校验的图片信息
     */
    void validPicture(Picture picture);

    /**
     * 图片审核
     * 根据传入的图片审核请求对象和用户登录信息，对图片进行审核操作
     *
     * @param pictureReviewRequest 图片审核请求对象，包含审核结果和审核信息
     * @param loginUser 当前登录的用户信息，用于记录审核者
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     * 根据传入的图片实体和用户登录信息，为图片实体添加审核相关信息
     *
     * @param picture 图片实体对象，用于添加审核相关信息
     * @param loginUser 当前登录的用户信息，用于记录审核者
     */
    void fillReviewParams(Picture picture, User loginUser);

    void checkPictureAuth(User loginUser, Picture picture);

    void deletePicture(long pictureId, User loginUser);

    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    @Async
    void clearPictureFile(Picture oldPicture);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(
            PictureUploadByBatchRequest pictureUploadByBatchRequest,
            User loginUser
    );

}
