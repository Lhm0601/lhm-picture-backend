package com.lhm.lhmpicturebackend.manager.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.lhm.lhmpicturebackend.manager.auth.SpaceUserAuthManager;
import com.lhm.lhmpicturebackend.manager.auth.model.SpaceUserPermissionConstant;
import com.lhm.lhmpicturebackend.model.entity.Picture;
import com.lhm.lhmpicturebackend.model.entity.Space;
import com.lhm.lhmpicturebackend.model.entity.User;
import com.lhm.lhmpicturebackend.model.enums.SpaceTypeEnum;
import com.lhm.lhmpicturebackend.service.PictureService;
import com.lhm.lhmpicturebackend.service.SpaceService;
import com.lhm.lhmpicturebackend.service.UserService;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * WebSocket握手拦截器
 * 用于在握手前验证用户身份和权限，并设置必要的属性
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 握手前的处理
     *
     * @param request 服务器HTTP请求
     * @param response 服务器HTTP响应
     * @param wsHandler WebSocket处理器
     * @param attributes 用于设置WebSocket会话属性的字典
     * @return 如果握手成功则返回true，否则返回false
     */
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
        // 确保请求是Servlet请求
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            // 获取请求参数
            String pictureId = servletRequest.getParameter("pictureId");
            // 验证图片参数是否存在
            if (StrUtil.isBlank(pictureId)) {
                log.error("缺少图片参数，拒绝握手");
                return false;
            }
            // 验证用户是否已登录
            User loginUser = userService.getLoginUser(servletRequest);
            if (ObjUtil.isEmpty(loginUser)) {
                log.error("用户未登录，拒绝握手");
                return false;
            }
            // 校验用户是否有该图片的权限
            Picture picture = pictureService.getById(pictureId);
            if (picture == null) {
                log.error("图片不存在，拒绝握手");
                return false;
            }
            Long spaceId = picture.getSpaceId();
            Space space = null;
            if (spaceId != null) {
                space = spaceService.getById(spaceId);
                // 验证空间是否存在
                if (space == null) {
                    log.error("空间不存在，拒绝握手");
                    return false;
                }
                // 验证是否为团队空间
                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
                    log.info("不是团队空间，拒绝握手");
                    return false;
                }
            }
            // 获取用户权限列表
            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
            // 验证用户是否有图片编辑权限
            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
                log.error("没有图片编辑权限，拒绝握手");
                return false;
            }
            // 设置 attributes
            attributes.put("user", loginUser);
            attributes.put("userId", loginUser.getId());
            attributes.put("pictureId", Long.valueOf(pictureId)); // 记得转换为 Long 类型
        }
        return true;
    }

    /**
     * 握手后的处理
     *
     * @param request 服务器HTTP请求
     * @param response 服务器HTTP响应
     * @param wsHandler WebSocket处理器
     * @param exception 握手过程中可能发生的异常
     */
    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {
        // 目前握手后不需要额外处理
    }
}
