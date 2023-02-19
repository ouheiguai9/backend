package com.byakuya.boot.backend.component.dfb.lawyer;

/**
 * Created by 田伯光 at 2023/2/8 11:13
 */
public enum LawyerState {
    CREATED("已注册") {
        @Override
        public LawyerState transition(LawyerAction action) {
            return action == LawyerAction.SUBMIT ? NOT_APPROVED : this;
        }
    },
    NOT_APPROVED("未审核") {
        @Override
        public LawyerState transition(LawyerAction action) {
            return action == LawyerAction.APPROVED ? OFF_DUTY : this;
        }
    },
    OFF_DUTY("下班") {
        @Override
        public LawyerState transition(LawyerAction action) {
            return action == LawyerAction.ON ? ON_DUTY : this;
        }
    },
    ON_DUTY("上班") {
        @Override
        public LawyerState transition(LawyerAction action) {
            switch (action) {
                case OFF:
                    return OFF_DUTY;
                case START:
                    return WORKING;
            }
            return this;
        }
    },
    WORKING("接单中") {
        @Override
        public LawyerState transition(LawyerAction action) {
            return action == LawyerAction.END ? ON_DUTY : this;
        }
    };

    public final String text;

    LawyerState(String text) {
        this.text = text;
    }

    public abstract LawyerState transition(LawyerAction action);
}
