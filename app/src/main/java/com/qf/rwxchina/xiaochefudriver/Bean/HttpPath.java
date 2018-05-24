package com.qf.rwxchina.xiaochefudriver.Bean;

/**
 * 网络地址
 */
public class HttpPath {


//    public static final String URL = "http://182.131.21.120:8081/index.php/";//测试服务器
 public static final String URL = "http://xiaochefu.cdrwx.cn/index.php/";//正式服务器
//   public static final String URL = "http://192.168.0.200:8081/index.php/";//黄萍服指向
    public static final String UPDATEURL="http://xiaochefu.cdrwx.cn/Application/apk/xcf_drive_edition.php";//更新域名
    //报单下单
    public static final String NEEDDRIVER_PATH = URL +"Driver/Driver/createOrder";
    //报单结束代驾
    public static final String NEEDDRIVER_OVERGO=URL+"Driver/Driver/createOrderOverGo";
    //检查当前城市是否已经开通服务
    public static final String CHECKCITY = URL + "User/Api/checkCityName";
    //获取司机当前上下班状态
    public static final String WORK_STATE=URL+"Driver/driver/getDriverStatus";
    //开始上班/下班
    public static final String WORK_PATH = URL + "Driver/driver/driverbegin";
    //获取附近订单
    public static final String GETORDER_PATH = URL + "Indent/Api/grabIndent";
    //获取订单详情
    public static final String ORDER_DETAILS_PATH = URL + "Indent/Api/getIndentMoreforDriver";
    //接受订单
    public static final String AGREEORDER_PAHT = URL + "Indent/Api/agreeIndent";
    //获取验证码
    public static final String LOGIN_CODE = URL + "Driver/driver/getVerify";
    //登陆
    public static final String LOGIN_PHTH = URL + "Driver/driver/login";
    //开始等待
    public static final String Beginwait = URL + "Driver/driver/beginwait";
    //开始代驾
    public static final String Begingo = URL + "Driver/driver/begingo";
    //结束代驾
    public static final String Overgo = URL + "Driver/driver/overgo";
    //完成代驾
    public static final String PkayOK = URL + "Driver/driver/payok";
    //当前订单信息
    public static final String orderdata = URL + "Indent/Api/getIndentMoreforDriver";
    //我的账户
//    public static final String account_data = URL + "Driver/driver/account_data";
    public static final String account_data = URL + "Driver/Driver/driver_center";
    //司机详情
    public static final String driverinfo = URL + "Driver/driver/driverinfo";
    //用户评价
    public static final String drivercomment = URL + "Driver/driver/drivercomment";
    //司机保单
    public static final String report_message = URL + "Driver/Driver/report_message";
    //历史订单
    public static final String getorder = URL + "Driver/Driver/getorder";
    //更新司机位置
    public static final String UPDATEGPS = URL + "Driver/Api/updateGPS";
    //司机抢单
    public static final String agreeIndent = URL + "Indent/Api/agreeIndent";
    //司机拒绝订单
    public static final String rejectIndent = URL + "Indent/Api/rejectIndent";
    //司机取消订单
    public static final String order_cancle = URL + "Driver/driver/order_cancle";
    //版本更新
    public static final String update_for_driver = URL + "Version/Api/update_for_driver";
    //异常取单
    public static final String yc = URL + "Indent/Api/actionErrorIndent";
    //司机充值
    public static final String recharge = URL + "Driver/Api/recharge2";
    //获取附近司机位置
    public static final String GETDRIVER = URL + "User/Api/getNearDriver";
    //删除订单
    public static final String cancelorder = URL + "Indent/Api/deleteDriverIndent";
    //查看司机是否有未完成的订单
    public static final String getDriverNotfinishedIndent = URL + "Indent/Api/getDriverNotfinishedIndent";
    // 司机实时记录等待时间
    public static final String waitreLoad = URL + "Indent/Api/waitreLoad";
    //代驾中上报行驶数据
    public static final String upData = URL + "Driver/api/upData";
    //司机提现
    public static final String driverdrawal = URL + "Driver/driver/driverdrawal";

    //司机发起结伴返程
    public static final String togowith = URL + "Driver/driver/togowith";
    //结伴返程
    public static final String isgo = URL + "Driver/driver/isgo";
    //获取新消息
    public static final String getMessageNew = URL + "Message/Api/getMessageNew";
    //获取历史消息
    public static final String getMessageList = URL +"Message/Api/getMessageList";
    //获取详细
    public static final String getMinute=URL+"Driver/driver/tells/driverid/1";

    //获取司机充值明细
    public  static final String MINGXI=URL+"Driver/Api/mycenter";

    //获取代驾价格
    public static final String GETDRIVERPRICE = URL + "Indent/Api/getCityrule";
 //app更新
 public static final String UPAPP=URL+"Application/apk/xcf_drive_edition.php";
   //首页-获取附近司机
   public static final String MAIN_PATH = URL + "User/Api/getNearDriver";
   //我已收款
   public static final String GETMONEY = URL + "Indent/Api/wysk";
    //获取第三方服务平台收费规则
   public static final String GETPOSTAGE = URL + "Indent/Api/getserverconf";
    //查看司机是否有新公告消息未读
    public static final String SELECTMSGSTATUS=URL+"Message/Api/selectmsgstauts";
    //跳转H5查看消息详情
    public static final String INFO=URL+"Message/Api/info";
    //公告消息列表
    public static final String MESSAGELIST=URL+"Message/Api/getMessageList";
    //删除公告
    public static final  String MESSAGE_DELETE = URL + "Message/Api/deleteMessage";

}
