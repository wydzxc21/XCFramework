package com.xc.framework.bean;

import java.io.Serializable;

/**
 * Date：2020/9/14
 * Author：ZhangXuanChen
 * Description：反射字段bean
 */
public class FieldBean implements Serializable {
    private String original;//原名
    private String alias;//别名
    private boolean isKeyId;//是否反射主键
    private boolean isUnique;//是否约束字段

    public FieldBean() {
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isKeyId() {
        return isKeyId;
    }

    public void setKeyId(boolean keyId) {
        isKeyId = keyId;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    @Override
    public String toString() {
        return "FieldBean{" +
                "original='" + original + '\'' +
                ", alias='" + alias + '\'' +
                ", isKeyId=" + isKeyId +
                ", isUnique=" + isUnique +
                '}';
    }
}
