package cn.polister.dianmeetingsystem.constants;

public class OrderConstants {


    private OrderConstants() {}
    public static final String ORDER_STATUS_WAIT_PAY = "待支付";
    public static final String ORDER_STATUS_PAYED = "已支付";
    public static final String ORDER_STATUS_CANCELED = "已取消";
    public static final String ORDER_STATUS_EXPIRED = "已过期";
    public static final String ORDER_STATUS_CANCELING = "退款中";
    public static final String ORDER_STATUS_REJECT_CANCEL = "拒绝退款";

    public static final String ROOM_REDIS_LOCK_KEY = "room:lock:";
    public static final Long ORDER_REDIS_KEY_EXPIRED = 10000L;

    public static final String ORDER_REDIS_LOCK_KEY = "order:lock:";

    public static final String ORDER_REDIS_LOCK_KEY_SCHEDULED = "order:scheduled";
}
