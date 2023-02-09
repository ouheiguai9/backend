package com.byakuya.boot.backend.component.dfb.order;

/**
 * Created by 田伯光 at 2023/2/9 15:29
 */
public enum OrderState {
    CREATED("已创建") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            switch (action) {
                case CALLBACK_SUCCESS:
                    return LAWYER_RESPONSE;
                case CALLBACK_FAIL:
                    return LAWYER_REJECT;
                case NO_CALLBACK:
                    return CALLBACK_OVERTIME;
            }
            return this;
        }
    },
    LAWYER_RESPONSE("律师接听") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            switch (action) {
                case HANG_UP:
                    return UNCONNECTED;
                case CONNECTED:
                    return CALLING;
                case WAIT_TIMEOUT:
                    return CONNECTED_TIMEOUT;
            }
            return this;
        }
    },
    LAWYER_REJECT("律师未接") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    CALLBACK_OVERTIME("回调超时") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    CALLING("通话中") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            switch (action) {
                case HANG_UP:
                    return (context instanceof Long && (Long) context >= 90L) ? UN_PAY : COMPLETED;
                case NO_HANG_UP:
                    return CALLING_TIMEOUT;
            }
            return this;
        }
    },
    CONNECTED_TIMEOUT("接通超时") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    UNCONNECTED("未接通") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    CALLING_TIMEOUT("通话超时") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    COMPLETED("完成") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    UN_PAY("未支付") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return action == OrderAction.PAYMENT ? PAID : this;
        }
    },
    NO_PAY("免单") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    },
    PAID("已支付") {
        @Override
        public OrderState transition(OrderAction action, Object context) {
            return this;
        }
    };

    public final String text;

    OrderState(String text) {
        this.text = text;
    }

    public abstract OrderState transition(OrderAction action, Object context);
}
