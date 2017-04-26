package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class BeizhuListBean implements Serializable{

    /**
     * status : ok
     * message :
     * data : [{"field_id":1,"customer_id":26500,"field_type":"text","field_name":"height","create_uid":261,"create_time":"2017-04-20 10:37:48","modify_uid":null,"modify_time":null,"create_name":"zhangdanfeng","modify_name":"System","last_add_name":null,"last_add_time":null,"last_add_value":null},{"field_id":2,"customer_id":26500,"field_type":"text","field_name":"weight","create_uid":261,"create_time":"2017-04-20 10:38:25","modify_uid":null,"modify_time":null,"create_name":"zhangdanfeng","modify_name":"System","last_add_name":null,"last_add_time":null,"last_add_value":null}]
     */

    private String status;
    private String message;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * field_id : 1
         * customer_id : 26500
         * field_type : text
         * field_name : height
         * create_uid : 261
         * create_time : 2017-04-20 10:37:48
         * modify_uid : null
         * modify_time : null
         * create_name : zhangdanfeng
         * modify_name : System
         * last_add_name : null
         * last_add_time : null
         * last_add_value : null
         */

        private int field_id;
        private int customer_id;
        private String field_type;
        private String field_name;
        private int create_uid;
        private String create_time;
        private Object modify_uid;
        private Object modify_time;
        private String create_name;
        private String modify_name;
        private Object last_add_name;
        private Object last_add_time;
        private Object last_add_value;

        public int getField_id() {
            return field_id;
        }

        public void setField_id(int field_id) {
            this.field_id = field_id;
        }

        public int getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(int customer_id) {
            this.customer_id = customer_id;
        }

        public String getField_type() {
            return field_type;
        }

        public void setField_type(String field_type) {
            this.field_type = field_type;
        }

        public String getField_name() {
            return field_name;
        }

        public void setField_name(String field_name) {
            this.field_name = field_name;
        }

        public int getCreate_uid() {
            return create_uid;
        }

        public void setCreate_uid(int create_uid) {
            this.create_uid = create_uid;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public Object getModify_uid() {
            return modify_uid;
        }

        public void setModify_uid(Object modify_uid) {
            this.modify_uid = modify_uid;
        }

        public Object getModify_time() {
            return modify_time;
        }

        public void setModify_time(Object modify_time) {
            this.modify_time = modify_time;
        }

        public String getCreate_name() {
            return create_name;
        }

        public void setCreate_name(String create_name) {
            this.create_name = create_name;
        }

        public String getModify_name() {
            return modify_name;
        }

        public void setModify_name(String modify_name) {
            this.modify_name = modify_name;
        }

        public Object getLast_add_name() {
            return last_add_name;
        }

        public void setLast_add_name(Object last_add_name) {
            this.last_add_name = last_add_name;
        }

        public Object getLast_add_time() {
            return last_add_time;
        }

        public void setLast_add_time(Object last_add_time) {
            this.last_add_time = last_add_time;
        }

        public Object getLast_add_value() {
            return last_add_value;
        }

        public void setLast_add_value(Object last_add_value) {
            this.last_add_value = last_add_value;
        }
    }
}
