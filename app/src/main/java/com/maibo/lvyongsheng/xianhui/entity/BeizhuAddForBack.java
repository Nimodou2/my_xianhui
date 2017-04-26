package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by Administrator on 2017/4/20.
 */

public class BeizhuAddForBack {

    /**
     * status : ok
     * message :
     * data : {"field_id":"4"}
     */

    private String status;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * field_id : 4
         */

        private String field_id;

        public String getField_id() {
            return field_id;
        }

        public void setField_id(String field_id) {
            this.field_id = field_id;
        }
    }
}
