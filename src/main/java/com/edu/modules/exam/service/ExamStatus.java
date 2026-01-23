package com.edu.modules.exam.service;

/**
 * 考试状态枚举
 */
public enum ExamStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    ENDED("已结束"),
    TIMEOUT("已超时");

    private final String desc;

    ExamStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}