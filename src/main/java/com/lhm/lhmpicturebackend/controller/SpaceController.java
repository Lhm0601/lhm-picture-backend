package com.lhm.lhmpicturebackend.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lhm.lhmpicturebackend.annotation.AuthCheck;
import com.lhm.lhmpicturebackend.common.BaseResponse;
import com.lhm.lhmpicturebackend.common.DeleteRequest;
import com.lhm.lhmpicturebackend.common.ResultUtils;
import com.lhm.lhmpicturebackend.constant.UserConstant;
import com.lhm.lhmpicturebackend.exception.BusinessException;
import com.lhm.lhmpicturebackend.exception.ErrorCode;
import com.lhm.lhmpicturebackend.exception.ThrowUtils;
import com.lhm.lhmpicturebackend.model.dto.space.*;
import com.lhm.lhmpicturebackend.model.dto.space.SpaceUpdateRequest;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.enums.PictureReviewStatusEnum;
import com.lhm.lhmpicturebackend.model.enums.SpaceLevelEnum;
import com.lhm.lhmpicturebackend.model.vo.SpaceVO;
import com.lhm.lhmpicturebackend.service.SpaceService;
import com.lhm.lhmpicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private SpaceService spaceService; // 注入空间服务

    @Resource
    private UserService userService; // 注入用户服务


    /**
     * 添加空间
     *
     * @param spaceAddRequest 空间添加请求对象，包含空间信息
     * @param request         HTTP 请求对象，用于获取当前登录用户
     * @return 返回添加成功的空间 ID
     * @throws BusinessException 如果请求参数为空，抛出参数错误异常
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN) // 仅管理员可访问
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest, HttpServletRequest request) {
        if (spaceAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // 参数校验
        }
        long space = spaceService.addSpace(spaceAddRequest, userService.getLoginUser(request)); // 调用服务层添加空间
        return ResultUtils.success(space); // 返回成功响应
    }

    /**
     * 更新空间
     *
     * @param spaceUpdateRequest 空间更新请求对象，包含更新后的空间信息
     * @return 返回更新是否成功
     * @throws BusinessException 如果请求参数为空或 ID 无效，抛出参数错误异常
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN) // 仅管理员可访问
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
        if (spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // 参数校验
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space); // 将 DTO 转换为实体类
        spaceService.fillSpaceBySpaceLevel(space); // 自动填充空间级别数据
        spaceService.validSpace(space, false); // 数据校验
        long id = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(id); // 查询旧空间数据
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR); // 检查空间是否存在
        boolean result = spaceService.updateById(space); // 更新空间
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 检查更新是否成功
        return ResultUtils.success(true); // 返回成功响应
    }

    /**
     * 删除空间
     *
     * @param deleteRequest 删除请求对象，包含待删除空间的 ID
     * @param request       HTTP 请求对象，用于获取当前登录用户
     * @return 返回删除是否成功
     * @throws BusinessException 如果请求参数为空或 ID 无效，抛出参数错误异常
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // 参数校验
        }
        User loginUser = userService.getLoginUser(request); // 获取当前登录用户
        long id = deleteRequest.getId();
        Space oldSpace = spaceService.getById(id); // 查询旧空间数据
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR); // 检查空间是否存在
        if (!oldSpace.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR); // 权限校验
        }
        boolean result = spaceService.removeById(id); // 删除空间
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 检查删除是否成功
        return ResultUtils.success(true); // 返回成功响应
    }

    /**
     * 根据 ID 获取空间（仅管理员可用）
     *
     * @param id      空间 ID
     * @param request HTTP 请求对象
     * @return 返回空间信息
     * @throws BusinessException 如果 ID 无效或空间不存在，抛出异常
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN) // 仅管理员可访问
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR); // 参数校验
        Space space = spaceService.getById(id); // 查询空间
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR); // 检查空间是否存在
        return ResultUtils.success(space); // 返回成功响应
    }

    /**
     * 根据 ID 获取空间（封装类）
     *
     * @param id      空间 ID
     * @param request HTTP 请求对象
     * @return 返回空间封装类信息
     * @throws BusinessException 如果 ID 无效或空间不存在，抛出异常
     */
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR); // 参数校验
        Space space = spaceService.getById(id); // 查询空间
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR); // 检查空间是否存在
        return ResultUtils.success(spaceService.getSpaceVO(space, request)); // 返回封装类
    }

    /**
     * 分页获取空间列表（仅管理员可用）
     *
     * @param spaceQueryRequest 空间查询请求对象，包含分页参数和查询条件
     * @return 返回分页空间列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.USER_ROLE_ADMIN) // 仅管理员可访问
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        long current = spaceQueryRequest.getCurrent(); // 获取当前页码
        long size = spaceQueryRequest.getPageSize(); // 获取每页大小
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest)); // 查询分页数据
        return ResultUtils.success(spacePage); // 返回成功响应
    }

    /**
     * 分页获取空间列表（封装类）
     *
     * @param spaceQueryRequest 空间查询请求对象，包含分页参数和查询条件
     * @param request           HTTP 请求对象
     * @return 返回分页空间封装类列表
     * @throws BusinessException 如果每页大小超过限制，抛出异常
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
                                                         HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent(); // 获取当前页码
        long size = spaceQueryRequest.getPageSize(); // 获取每页大小
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR); // 限制每页大小
        Page<Space> spacePage = spaceService.page(new Page<>(current, size),
                spaceService.getQueryWrapper(spaceQueryRequest)); // 查询分页数据
        return ResultUtils.success(spaceService.getSpaceVOPage(spacePage, request)); // 返回封装类
    }

    /**
     * 获取空间级别列表
     *
     * @return 返回空间级别列表
     */
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values()) // 获取所有枚举
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList()); // 转换为列表
        return ResultUtils.success(spaceLevelList); // 返回成功响应
    }

    /**
     * 编辑空间（给用户使用）
     *
     * @param spaceEditRequest 空间编辑请求对象，包含编辑后的空间信息
     * @param request          HTTP 请求对象，用于获取当前登录用户
     * @return 返回编辑是否成功
     * @throws BusinessException 如果请求参数为空或 ID 无效，抛出参数错误异常
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        if (spaceEditRequest == null || spaceEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR); // 参数校验
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space); // 将 DTO 转换为实体类
        spaceService.fillSpaceBySpaceLevel(space); // 自动填充空间级别数据
        space.setEditTime(new Date()); // 设置编辑时间
        spaceService.validSpace(space, false); // 数据校验
        User loginUser = userService.getLoginUser(request); // 获取当前登录用户
        long id = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(id); // 查询旧空间数据
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR); // 检查空间是否存在
        if (!oldSpace.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR); // 权限校验
        }
        boolean result = spaceService.updateById(space); // 更新空间
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR); // 检查更新是否成功
        return ResultUtils.success(true); // 返回成功响应
    }
}
