package com.foodtraffic.user.model.entity;

public enum UserStatus {
    ACTIVE(0),
    INACTIVE(1),
    HOLD(2);

    private int statusNum;

    UserStatus(int statusNum) {
        this.statusNum = statusNum;
    }

    public int getStatusNum() {
        return statusNum;
    }
}
