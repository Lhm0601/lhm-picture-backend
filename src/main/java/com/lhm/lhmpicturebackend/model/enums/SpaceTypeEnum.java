package com.lhm.lhmpicturebackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间类型枚举
 */
@Getter
public enum SpaceTypeEnum {

    /**
     * 私有空间
     */
    PRIVATE("私有空间", 0),
    /**
     * 团队空间
     */
    TEAM("团队空间", 1);

    private final String text;

    private final int value;

    /**
     * 构造方法
     *
     * @param text 文本描述
     * @param value 数值表示
     */
    SpaceTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 数值表示
     * @return 对应的枚举类型，如果找不到匹配的则返回 null
     */
    public static SpaceTypeEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum spaceTypeEnum : SpaceTypeEnum.values()) {
            if (spaceTypeEnum.value == value) {
                return spaceTypeEnum;
            }
        }
        return null;
    }
}
