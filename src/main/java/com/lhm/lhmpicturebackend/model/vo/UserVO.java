package com.lhm.lhmpicturebackend.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    //id
    private Long id;
    //用户昵称
    private String userName;
    //账号
    private String userAccount;
    //用户头像
    private String userAvatar;
    //用户简介
    private String userProfile;
    //用户角色：user/admin/ban
    private String userRole;
    //创建时间
    private Long createTime;
    private static final long serialVersionUID = 1L;

}
