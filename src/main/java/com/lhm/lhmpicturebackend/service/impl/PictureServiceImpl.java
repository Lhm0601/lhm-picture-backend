package com.lhm.lhmpicturebackend.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.manager.CosManager;
import com.lhm.lhmpicturebackend.manager.upload.FilePictureUpload;
import com.lhm.lhmpicturebackend.manager.upload.PictureUploadTemplate;
import com.lhm.lhmpicturebackend.manager.upload.UrlPictureUpload;
import com.lhm.lhmpicturebackend.mapper.PictureMapper;
import com.lhm.lhmpicturebackend.model.dto.file.UploadPictureResult;
import com.lhm.lhmpicturebackend.model.dto.picture.*;
import com.lhm.lhmpicturebackend.model.entity.Picture;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.enums.PictureReviewStatusEnum;
import com.lhm.lhmpicturebackend.model.vo.PictureVO;
import com.lhm.lhmpicturebackend.model.vo.UserVO;
import com.lhm.lhmpicturebackend.service.PictureService;
import com.lhm.lhmpicturebackend.service.SpaceService;
import com.lhm.lhmpicturebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 梁
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2024-12-12 13:58:50
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {
    @Resource
    UserService userService;

    @Resource
    private CosManager cosManager;

    @Resource
    private SpaceService spaceService;

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;
    @Resource
    private TransactionTemplate transactionTemplate;

    /**
     * 上传图片方法
     *
     * @param inputSource          图片文件，用于上传
     * @param pictureUploadRequest 图片上传请求对象，可能包含图片ID，用于判断是新增还是更新图片
     * @param loginUser            登录用户信息，用于验证权限和确定图片存储路径
     * @return 返回上传后的图片信息对象
     * <p>
     * 该方法首先验证用户是否有权限进行操作，然后根据请求中的图片ID判断是新增还是更新图片
     * 如果是更新操作，会检查图片是否已存在如果不存在，则抛出错误
     * 接着，方法构造图片的上传路径，并调用文件管理器上传图片
     * 上传成功后，将图片信息保存或更新到数据库中，并返回上传后的图片信息
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 检查用户是否已登录，未登录则抛出无权限错误
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 空间权限校验
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            // 必须空间创建人（管理员）才能上传
            if (!loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            }
            // 校验额度
            if (space.getTotalCount() >= space.getMaxCount()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间条数不足");
            }
            if (space.getTotalSize() >= space.getMaxSize()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
            }
        }


        // 用于判断是新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }

        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            //仅本人或管理员可编辑
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }

            //校验空间是否一致
            //没传spaceId，则服用原有图片的spaceId
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    spaceId = oldPicture.getSpaceId();
                }
            } else {
                //传了spaceId，则校验是否一致
                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间id不一致");
                }
            }
        }
        // 上传图片，得到信息
        // 按照用户 id 划分目录=>按照空间划分目录
        String uploadPathPrefix;
        if (spaceId != null) {
            uploadPathPrefix = String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }


        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);

        // 构造要入库的图片信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        String picName = uploadPictureResult.getPicName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);
        //补充设置 sapaceId
        picture.setSpaceId(spaceId);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        //补充审核参数
        fillReviewParams(picture, loginUser);
        // 如果 pictureId 不为空，表示更新，否则是新增
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 开启事务
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            boolean result = this.saveOrUpdate(picture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
            if (finalSpaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return picture;
        });


        // 返回上传后的图片信息对象
        return PictureVO.objToVo(picture);
    }

    /**
     * 生成Picture实体的查询包装器
     * 此方法根据PictureQueryRequest中的查询请求参数，构建一个QueryWrapper对象，用于后续的数据库查询操作
     * 它支持多条件组合查询、模糊查询、排序等功能
     *
     * @param pictureQueryRequest 包含查询条件的请求对象，如果为null，则返回一个空的QueryWrapper对象
     * @return 返回一个构建好的QueryWrapper对象，用于执行数据库查询
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        // 构建查询条件，仅当字段值不为空时才添加相应条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        queryWrapper.isNull(nullSpaceId, "spaceId");

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 根据Picture对象获取封装的PictureVO对象
     * 此方法首先将Picture对象转换为PictureVO对象，然后关联查询用户信息并进行封装
     *
     * @param picture Picture对象，包含图片相关信息
     * @param request HttpServletRequest对象，用于获取请求相关数据
     * @return PictureVO对象，包含封装的图片及用户信息
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        // 获取图片列表
        List<Picture> pictureList = picturePage.getRecords();
        // 初始化图片VO分页对象
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        // 如果图片列表为空，则直接返回空的VO分页对象
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 校验图片信息的有效性
     * 此方法主要用于确保提供的图片信息在更新或处理时是有效的，包括检查图片的ID、URL和简介是否符合规范
     *
     * @param picture 图片对象，包含图片的相关信息，如ID、URL和简介
     * @throws IllegalArgumentException 如果图片对象为null或图片的ID为空，或URL、简介超过规定长度，则抛出异常
     */
    @Override
    public void validPicture(Picture picture) {
        // 检查图片对象是否为null，如果是，则抛出参数错误异常
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);

        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();

        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");

        // 如果URL不为空，检查URL长度是否超过1024字符
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }

        // 如果简介不为空，检查简介长度是否超过800字符
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 执行图片审核操作
     * 此方法负责根据用户输入的审核状态更新图片的审核状态
     * 它首先验证输入参数的有效性，然后获取图片的当前状态，
     * 最后更新图片的审核状态和相关信息
     *
     * @param pictureReviewRequest 包含待审核图片ID和审核状态的请求对象
     * @param loginUser            执行审核操作的用户信息
     * @throws BusinessException 当输入参数无效或图片已审核时抛出业务异常
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 获取审核请求中的图片ID和审核状态
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        // 根据审核状态值获取对应的枚举类型
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        // 验证图片ID和审核状态的有效性，防止重复审核
        if (id == null || reviewStatusEnum == null || PictureReviewStatusEnum.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 根据ID获取图片对象
        Picture oldPicture = getById(id);
        // 如果图片不存在，抛出异常
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 如果图片已经是审核状态，抛出异常防止重复审核
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片已审核");
        }
        // 创建一个新的图片对象用于更新
        Picture updatePicture = new Picture();
        // 复制请求中的属性到新的图片对象
        BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
        // 设置审核人的ID为当前登录用户的ID
        updatePicture.setReviewerId(loginUser.getId());
        // 设置编辑时间为当前时间
        updatePicture.setEditTime(new Date());
        // 更新数据库中的图片信息
        boolean result = this.updateById(updatePicture);
        // 如果更新失败，抛出异常
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    /**
     * 补充审核参数
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            //管理员自动过审
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewMessage("管理员自动过审");
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
        } else {
            //非管理员，创建或者编辑都要改为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    /**
     * 删除图片权限校验
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 公共图库，仅本人或管理员可操作
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            // 私有空间，仅空间管理员可操作
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    /**
     * 删除图片
     */
    @Override
    public void deletePicture(long pictureId, User loginUser) {
        ThrowUtils.throwIf(pictureId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 判断是否存在
        Picture oldPicture = this.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(pictureId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 释放额度
            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalSize = totalSize - " + oldPicture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return true;
        });

        // 异步清理文件
        this.clearPictureFile(oldPicture);
    }

    @Override
    public void editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 补充审核参数
        this.fillReviewParams(picture, loginUser);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }


//    @Override
//    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
//        String searchText = pictureUploadByBatchRequest.getSearchText();
//        // 格式化数量
//        Integer count = pictureUploadByBatchRequest.getCount();
//        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");
//        // 要抓取的地址
//        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
//        Document document;
//        try {
//            document = Jsoup.connect(fetchUrl).get();
//        } catch (IOException e) {
//            log.error("获取页面失败", e);
//            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
//        }
//        Element div = document.getElementsByClass("dgControl").first();
//        if (ObjUtil.isNull(div)) {
//            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
//        }
//        Elements imgElementList = div.select("img.mimg");
//        int uploadCount = 0;
//        for (Element imgElement : imgElementList) {
//            String fileUrl = imgElement.attr("src");
//            if (StrUtil.isBlank(fileUrl)) {
//                log.info("当前链接为空，已跳过: {}", fileUrl);
//                continue;
//            }
//            // 处理图片上传地址，防止出现转义问题
//            int questionMarkIndex = fileUrl.indexOf("?");
//            if (questionMarkIndex > -1) {
//                fileUrl = fileUrl.substring(0, questionMarkIndex);
//            }
//            String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
//            if (StrUtil.isBlank(namePrefix)) {
//                namePrefix = searchText;
//            }
//            // 上传图片
//            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
//            if (StrUtil.isNotBlank(namePrefix)) {
//                // 设置图片名称，序号连续递增
//                pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
//            }
//            try {
//                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
//                log.info("图片上传成功, id = {}", pictureVO.getId());
//                uploadCount++;
//            } catch (Exception e) {
//                log.error("图片上传失败", e);
//                continue;
//            }
//            if (uploadCount >= count) {
//                break;
//            }
//        }
//        return uploadCount;
//    }

    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        // 判断该图片是否被多条记录使用
        String pictureUrl = oldPicture.getUrl();
        long count = this.lambdaQuery()
                .eq(Picture::getUrl, pictureUrl)
                .count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 1) {
            return;
        }
        cosManager.deleteObject(oldPicture.getUrl());
        // 清理缩略图
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            cosManager.deleteObject(thumbnailUrl);
        }
    }


    /**
     * 通过批量方式上传图片
     *
     * @param pictureUploadByBatchRequest 批量上传图片的请求对象，包含搜索文本和图片数量
     * @param loginUser                   登录用户信息，用于记录操作者
     * @return 成功上传的图片数量
     */
    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 获取搜索文本
        String searchText = pictureUploadByBatchRequest.getSearchText();
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        // 检查请求的数量是否超过最大限制
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");

        // 初始化已上传图片的总数
        int totalUploaded = 0;
        // 设置每次查询的图片数量
        int pageSize = 30; // 每次查询 30 张图片

        // 根据需求的图片数量，循环查询和上传图片
        for (int page = 1; totalUploaded < count; page++) {
            // 构造请求百度图片搜索API的URL
            String urlTemplate = "https://images.baidu.com/search/acjson?tn=resultjson_com&word=%s&pn=%s";
            String url = String.format(urlTemplate, searchText, (page - 1) * pageSize);

            // 发送HTTP GET请求获取图片搜索结果
            String response = HttpUtil.get(url);
            // 解析响应JSON对象
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 获取图片数据列表
            JSONArray dataList = jsonObject.getJSONArray("data");

            // 检查是否有更多图片可抓取
            if (dataList == null || dataList.isEmpty()) {
                log.info("没有更多图片可抓取");
                break;
            }

            // 遍历图片数据列表
            for (int i = 0; i < dataList.size() && totalUploaded < count; i++) {
                JSONObject dataItem = dataList.getJSONObject(i);
                // 检查数据项是否非空
                if (dataItem != null) {
                    // 获取图片的缩略图URL
                    String thumbURL = dataItem.getStr("thumbURL");
                    // 检查URL是否非空
                    if (StrUtil.isNotBlank(thumbURL)) {
                        // 获取图片名称前缀
                        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
                        // 如果名称前缀为空，则使用搜索文本作为前缀
                        if (StrUtil.isBlank(namePrefix)) {
                            namePrefix = searchText;
                        }

                        // 创建图片上传请求对象
                        PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
                        // 设置图片名称，使用前缀和序号组成
                        pictureUploadRequest.setPicName(namePrefix + (totalUploaded + 1));

                        try {
                            // 调用上传图片方法
                            PictureVO pictureVO = this.uploadPicture(thumbURL, pictureUploadRequest, loginUser);
                            // 记录日志
                            log.info("图片上传成功, id = {}", pictureVO.getId());
                            // 更新已上传图片的总数
                            totalUploaded++;
                        } catch (Exception e) {
                            // 记录错误日志
                            log.error("图片上传失败", e);
                        }
                    }
                }
            }
        }
        // 返回成功上传的图片数量
        return totalUploaded;
    }
}

