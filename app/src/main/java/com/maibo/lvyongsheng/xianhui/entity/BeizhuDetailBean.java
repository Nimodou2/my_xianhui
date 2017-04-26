package com.maibo.lvyongsheng.xianhui.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class BeizhuDetailBean implements Serializable{

    /**
     * status : ok
     * message :
     * data : [{"field_id":4,"line":1,"field_value":"他家里很多车","create_uid":261,"create_time":"2017-04-20 13:01:27","create_name":"zhangdanfeng","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=profile&uid=261&rand=20170413085900"}]
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
         * field_id : 4
         * line : 1
         * field_value : 他家里很多车
         * create_uid : 261
         * create_time : 2017-04-20 13:01:27
         * create_name : zhangdanfeng
         * avator_url : http://mybook.sosys.cn:8080/php/get.php?act=profile&uid=261&rand=20170413085900
         */

        private int field_id;
        private int line;
        private String field_value;
        private int create_uid;
        private String create_time;
        private String create_name;
        private String avator_url;

        public int getField_id() {
            return field_id;
        }

        public void setField_id(int field_id) {
            this.field_id = field_id;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getField_value() {
            return field_value;
        }

        public void setField_value(String field_value) {
            this.field_value = field_value;
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

        public String getCreate_name() {
            return create_name;
        }

        public void setCreate_name(String create_name) {
            this.create_name = create_name;
        }

        public String getAvator_url() {
            return avator_url;
        }

        public void setAvator_url(String avator_url) {
            this.avator_url = avator_url;
        }
    }
}
