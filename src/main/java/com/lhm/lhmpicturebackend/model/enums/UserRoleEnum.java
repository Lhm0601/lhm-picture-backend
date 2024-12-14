package com.lhm.lhmpicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户角色枚举类，用于定义系统中的用户角色
 */
@Getter
public enum UserRoleEnum {

    /**
     * 普通用户角色
     */
    USER("用户", "user"),
    /**
     * 管理员角色
     */
    ADMIN("管理员", "admin");

    /**
     * 角色的文本描述
     */
    private final String text;

    /**
     * 角色的值，用于内部标识角色
     */
    private final String value;

    /**
     * 构造函数，初始化角色的文本描述和值
     *
     * @param text 角色的文本描述
     * @param value 角色的值
     */
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据角色值获取对应的枚举实例
     *
     * @param value 角色的值
     * @return 对应的枚举实例，如果找不到匹配的则返回null
     */
    public static UserRoleEnum getEnumByValue(String value) {
        // 检查传入的值是否为空，为空则直接返回null
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        // 遍历所有枚举实例，寻找匹配的值
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        // 如果没有找到匹配的值，返回null
        return null;
    }
}
