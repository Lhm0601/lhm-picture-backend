package com.lhm.lhmpicturebackend.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lhm.lhmpicturebackend.model.dto.space.SpaceAddRequest;
import com.lhm.lhmpicturebackend.model.dto.space.SpaceQueryRequest;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 梁
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2024-12-23 16:56:45
*/
public interface SpaceService extends IService<Space> {


    /**
     * 构建空间查询条件
     * 根据传入的空间查询请求参数构建MyBatis-Plus的QueryWrapper对象，用于数据库查询
     *
     * @param SpaceQueryRequest 空间查询请求对象，包含一系列查询条件，如标签、上传者等
     * @return 返回一个QueryWrapper对象，用于执行数据库查询操作
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest SpaceQueryRequest);

    /**
     * 获取空间VO分页数据
     * 该方法将从数据库中查询到的空间记录转换为空间VO对象，并进行分页处理
     *
     * @param SpacePage 空间实体的分页对象，包含从数据库查询到的空间记录
     * @param request HTTP请求对象，用于获取请求上下文信息
     * @return 返回一个SpaceVO类型的分页对象，包含转换后的空间信息和分页数据
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> SpacePage, HttpServletRequest request);

    /**
     * 构建单个空间VO对象
     * 根据传入的空间实体和请求对象，构建一个空间VO对象，用于前端展示
     *
     * @param Space 空间实体对象，包含空间的基本信息
     * @param request HTTP请求对象，用于获取请求上下文信息
     * @return 返回一个SpaceVO对象，包含空间的详细信息和访问地址等
     */
    SpaceVO getSpaceVO(Space Space, HttpServletRequest request);

    void validSpace(Space space, boolean add);

    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    void fillSpaceBySpaceLevel(Space space);

}
