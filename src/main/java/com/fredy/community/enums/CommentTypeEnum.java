package com.fredy.community.enums;

import com.fredy.community.model.Comment;

public enum CommentTypeEnum {
    Question(1),
    COMMENT(2);

    private Integer type;
    CommentTypeEnum(Integer type){
        this.type = type;
    }

    public static boolean isExist(Integer type) {
        for (CommentTypeEnum commentTypeEnum : CommentTypeEnum.values()) {
            if(commentTypeEnum.getType() == type){
                return true;
            }
        }
        return false;
    }

    public Integer getType() {
        return type;
    }
}
