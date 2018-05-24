package com.qf.rwxchina.xiaochefudriver.Bean;

import java.util.List;

/**
 * Created by Administrator on 2016/12/22.
 */
public class My_account_calance_Info {

    /**
     * data : {"driver_account":{"income":"19585.60","spending":"0.00","balance":"49692.60"},"driver_bills":[{"id":"2834","driver_id":"102","add_date":"2016-12-23 10:55:13","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2833","driver_id":"102","add_date":"2016-12-23 10:54:55","income":"30.40","spending":"0.00","ordernumber":"s220185733776766","remark":"订单收入"},{"id":"2832","driver_id":"102","add_date":"2016-12-23 10:42:16","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2831","driver_id":"102","add_date":"2016-12-23 10:42:12","income":"30.40","spending":"0.00","ordernumber":"s220130330635891","remark":"订单收入"},{"id":"2830","driver_id":"102","add_date":"2016-12-23 09:37:33","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2829","driver_id":"102","add_date":"2016-12-23 09:37:13","income":"30.40","spending":"0.00","ordernumber":"s220106378479311","remark":"订单收入"}],"driver_order_data":[{"status":"7","id":"2768","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 09:35:52"},{"status":"7","id":"2769","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 10:36:41"},{"status":"7","id":"2773","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 10:54:47"}],"drawals":[{"id":"22","add_date":"2016-12-23 10:59:09","apply_money":"300.00","apply_status":"0"},{"id":"23","add_date":"2016-12-23 11:00:08","apply_money":"300.00","apply_status":"0"}]}
     * state : {"code":0,"msg":"","debugMsg":"","url":"Driver/Driver/driver_center"}
     */

    private DataBean data;
    private StateBean state;

    @Override
    public String toString() {
        return "My_account_calance_Info{" +
                "data=" + data +
                ", state=" + state +
                '}';
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public StateBean getState() {
        return state;
    }

    public void setState(StateBean state) {
        this.state = state;
    }

    public static class DataBean {
        /**
         * driver_account : {"income":"19585.60","spending":"0.00","balance":"49692.60"}
         * driver_bills : [{"id":"2834","driver_id":"102","add_date":"2016-12-23 10:55:13","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2833","driver_id":"102","add_date":"2016-12-23 10:54:55","income":"30.40","spending":"0.00","ordernumber":"s220185733776766","remark":"订单收入"},{"id":"2832","driver_id":"102","add_date":"2016-12-23 10:42:16","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2831","driver_id":"102","add_date":"2016-12-23 10:42:12","income":"30.40","spending":"0.00","ordernumber":"s220130330635891","remark":"订单收入"},{"id":"2830","driver_id":"102","add_date":"2016-12-23 09:37:33","income":"0.00","spending":null,"ordernumber":null,"remark":"平台费服务费结算"},{"id":"2829","driver_id":"102","add_date":"2016-12-23 09:37:13","income":"30.40","spending":"0.00","ordernumber":"s220106378479311","remark":"订单收入"}]
         * driver_order_data : [{"status":"7","id":"2768","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 09:35:52"},{"status":"7","id":"2769","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 10:36:41"},{"status":"7","id":"2773","original_totalmoney":"38","platformmoney":"-7.6","_fanli":"","endtime":"2016-12-23 10:54:47"}]
         * drawals : [{"id":"22","add_date":"2016-12-23 10:59:09","apply_money":"300.00","apply_status":"0"},{"id":"23","add_date":"2016-12-23 11:00:08","apply_money":"300.00","apply_status":"0"}]
         */

        private DriverAccountBean driver_account;
        private List<DriverBillsBean> driver_bills;
        private List<DriverOrderDataBean> driver_order_data;
        private List<DrawalsBean> drawals;

        @Override
        public String toString() {
            return "DataBean{" +
                    "driver_account=" + driver_account +
                    ", driver_bills=" + driver_bills +
                    ", driver_order_data=" + driver_order_data +
                    ", drawals=" + drawals +
                    '}';
        }

        public DriverAccountBean getDriver_account() {
            return driver_account;
        }

        public void setDriver_account(DriverAccountBean driver_account) {
            this.driver_account = driver_account;
        }

        public List<DriverBillsBean> getDriver_bills() {
            return driver_bills;
        }

        public void setDriver_bills(List<DriverBillsBean> driver_bills) {
            this.driver_bills = driver_bills;
        }

        public List<DriverOrderDataBean> getDriver_order_data() {
            return driver_order_data;
        }

        public void setDriver_order_data(List<DriverOrderDataBean> driver_order_data) {
            this.driver_order_data = driver_order_data;
        }

        public List<DrawalsBean> getDrawals() {
            return drawals;
        }

        public void setDrawals(List<DrawalsBean> drawals) {
            this.drawals = drawals;
        }

        public static class DriverAccountBean {
            /**
             * income : 19585.60
             * spending : 0.00
             * balance : 49692.60
             */

            private String income;
            private String spending;
            private String balance;

            @Override
            public String toString() {
                return "DriverAccountBean{" +
                        "income='" + income + '\'' +
                        ", spending='" + spending + '\'' +
                        ", balance='" + balance + '\'' +
                        '}';
            }

            public String getIncome() {
                return income;
            }

            public void setIncome(String income) {
                this.income = income;
            }

            public String getSpending() {
                return spending;
            }

            public void setSpending(String spending) {
                this.spending = spending;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }
        }

        public static class DriverBillsBean {
            /**
             * id : 2834
             * driver_id : 102
             * add_date : 2016-12-23 10:55:13
             * income : 0.00
             * spending : null
             * ordernumber : null
             * remark : 平台费服务费结算
             */

            private String id;
            private String driver_id;
            private String add_date;
            private String income;
            private Object spending;
            private Object ordernumber;
            private String remark;

            @Override
            public String toString() {
                return "DriverBillsBean{" +
                        "id='" + id + '\'' +
                        ", driver_id='" + driver_id + '\'' +
                        ", add_date='" + add_date + '\'' +
                        ", income='" + income + '\'' +
                        ", spending=" + spending +
                        ", ordernumber=" + ordernumber +
                        ", remark='" + remark + '\'' +
                        '}';
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDriver_id() {
                return driver_id;
            }

            public void setDriver_id(String driver_id) {
                this.driver_id = driver_id;
            }

            public String getAdd_date() {
                return add_date;
            }

            public void setAdd_date(String add_date) {
                this.add_date = add_date;
            }

            public String getIncome() {
                return income;
            }

            public void setIncome(String income) {
                this.income = income;
            }

            public Object getSpending() {
                return spending;
            }

            public void setSpending(Object spending) {
                this.spending = spending;
            }

            public Object getOrdernumber() {
                return ordernumber;
            }

            public void setOrdernumber(Object ordernumber) {
                this.ordernumber = ordernumber;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }

        public static class DriverOrderDataBean {
            /**
             * status : 7
             * id : 2768
             * original_totalmoney : 38
             * platformmoney : -7.6
             * _fanli :
             * endtime : 2016-12-23 09:35:52
             */

            private String status;
            private String id;
            private String original_totalmoney;
            private String platformmoney;
            private String _fanli;
            private String endtime;

            @Override
            public String toString() {
                return "DriverOrderDataBean{" +
                        "status='" + status + '\'' +
                        ", id='" + id + '\'' +
                        ", original_totalmoney='" + original_totalmoney + '\'' +
                        ", platformmoney='" + platformmoney + '\'' +
                        ", _fanli='" + _fanli + '\'' +
                        ", endtime='" + endtime + '\'' +
                        '}';
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getOriginal_totalmoney() {
                return original_totalmoney;
            }

            public void setOriginal_totalmoney(String original_totalmoney) {
                this.original_totalmoney = original_totalmoney;
            }

            public String getPlatformmoney() {
                return platformmoney;
            }

            public void setPlatformmoney(String platformmoney) {
                this.platformmoney = platformmoney;
            }

            public String get_fanli() {
                return _fanli;
            }

            public void set_fanli(String _fanli) {
                this._fanli = _fanli;
            }

            public String getEndtime() {
                return endtime;
            }

            public void setEndtime(String endtime) {
                this.endtime = endtime;
            }
        }

        public static class DrawalsBean {
            /**
             * id : 22
             * add_date : 2016-12-23 10:59:09
             * apply_money : 300.00
             * apply_status : 0
             */

            private String id;
            private String add_date;
            private String apply_money;
            private String apply_status;

            @Override
            public String toString() {
                return "DrawalsBean{" +
                        "id='" + id + '\'' +
                        ", add_date='" + add_date + '\'' +
                        ", apply_money='" + apply_money + '\'' +
                        ", apply_status='" + apply_status + '\'' +
                        '}';
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAdd_date() {
                return add_date;
            }

            public void setAdd_date(String add_date) {
                this.add_date = add_date;
            }

            public String getApply_money() {
                return apply_money;
            }

            public void setApply_money(String apply_money) {
                this.apply_money = apply_money;
            }

            public String getApply_status() {
                return apply_status;
            }

            public void setApply_status(String apply_status) {
                this.apply_status = apply_status;
            }
        }
    }

    public static class StateBean {
        /**
         * code : 0
         * msg :
         * debugMsg :
         * url : Driver/Driver/driver_center
         */

        private int code;
        private String msg;
        private String debugMsg;
        private String url;

        @Override
        public String toString() {
            return "StateBean{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", debugMsg='" + debugMsg + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getDebugMsg() {
            return debugMsg;
        }

        public void setDebugMsg(String debugMsg) {
            this.debugMsg = debugMsg;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
