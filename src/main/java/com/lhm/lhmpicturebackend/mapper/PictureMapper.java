package com.lhm.lhmpicturebackend.mapper;

import com.lhm.lhmpicturebackend.model.dto.picture.PictureUploadRequest;
import com.lhm.lhmpicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 梁
* @description 针对表【picture(图片)】的数据库操作Mapper
* @createDate 2024-12-12 13:58:50
* @Entity com.lhm.lhmpicturebackend.model.entity.Picture
*/
public interface PictureMapper extends BaseMapper<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);
}




