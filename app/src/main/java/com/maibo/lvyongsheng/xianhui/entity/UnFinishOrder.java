package com.maibo.lvyongsheng.xianhui.entity;

import java.util.List;

/**
 * Created by LYS on 2017/3/16.
 */

public class UnFinishOrder {
    /**
     * status : ok
     * message :
     * data : {"total":986,"rows":[{"adate":"2017-03-16","customer_id":28004,"org_id":2,"customer_name":"汤丹燕","avator":null,"guid":"customer_1_28004","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%B9%E7%87%95","schedule_list":[{"schedule_id":106606,"org_id":2,"status":0,"start_time":"13:30","end_time":"15:00","engineer_id":"72","project_name":"水通道护理","status_text":"未开始","engineer_name":"郭周雨"}],"plan_list":null},{"adate":"2017-03-16","customer_id":28049,"org_id":2,"customer_name":"富静艳","avator":null,"guid":"customer_1_28049","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E9%9D%99%E8%89%B3","schedule_list":[{"schedule_id":106605,"org_id":2,"status":0,"start_time":"14:00","end_time":"15:30","engineer_id":"73","project_name":"水养","status_text":"未开始","engineer_name":"潘慧"},{"schedule_id":106604,"org_id":2,"status":0,"start_time":"11:30","end_time":"13:00","engineer_id":"69","project_name":"水通道护理","status_text":"未开始","engineer_name":"和珍"}],"plan_list":null},{"adate":"2017-03-15","customer_id":26514,"org_id":2,"customer_name":"虞志萍VIP","avator":null,"guid":"customer_1_26514","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E8%90%8DV","schedule_list":[{"schedule_id":106603,"org_id":2,"status":0,"start_time":"12:00","end_time":"13:00","engineer_id":"128","project_name":"哈尼药浴*4","status_text":"未开始","engineer_name":"包雪梅"},{"schedule_id":106602,"org_id":2,"status":0,"start_time":"10:30","end_time":"12:00","engineer_id":"73","project_name":"排","status_text":"未开始","engineer_name":"潘慧"}],"plan_list":[{"project_name":"哈尼药浴*5","creator":"zhangdanfeng","create_date":"2017-03-13"},{"project_name":"哈尼药浴*6","creator":"zhangdanfeng","create_date":"2017-03-13"},{"project_name":"纤体能量仪","creator":"zhangdanfeng","create_date":"2017-03-13"}]},{"adate":"2017-03-15","customer_id":27993,"org_id":2,"customer_name":"王奇","avator":null,"guid":"customer_1_27993","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E7%8E%8B%E5%A5%87","schedule_list":[{"schedule_id":106600,"org_id":2,"status":0,"start_time":"19:30","end_time":"20:30","engineer_id":"46","project_name":"哈尼药浴*2","status_text":"未开始","engineer_name":"刘仙"},{"schedule_id":106601,"org_id":2,"status":0,"start_time":"17:00","end_time":"18:00","engineer_id":"128","project_name":"哈尼药浴*1","status_text":"未开始","engineer_name":"包雪梅"},{"schedule_id":106598,"org_id":2,"status":0,"start_time":"12:30","end_time":"13:00","engineer_id":"67","project_name":"肌肉放松","status_text":"未开始","engineer_name":"李庆洁"}],"plan_list":null},{"adate":"2017-03-12","customer_id":28115,"org_id":2,"customer_name":"丁伟","avator":null,"guid":"customer_1_28115","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%81%E4%BC%9F","schedule_list":[{"schedule_id":106593,"org_id":2,"status":0,"start_time":"17:30","end_time":"18:00","engineer_id":"67","project_name":"魔镜","status_text":"未开始","engineer_name":"李庆洁"}],"plan_list":[{"project_name":"水养","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"魔镜","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"脱毒","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"种植","creator":"zhangdanfeng","create_date":"2017-03-12"}]},{"adate":"2017-03-10","customer_id":28076,"org_id":2,"customer_name":"金岚","avator":null,"guid":"customer_1_28076","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E9%87%91%E5%B2%9A","schedule_list":[{"schedule_id":106589,"org_id":2,"status":0,"start_time":"11:00","end_time":"12:30","engineer_id":"185","project_name":"平衡霜*1","status_text":"未开始","engineer_name":"李金兰"}],"plan_list":[{"project_name":"天地藏浴","creator":"吕永生","create_date":"2017-03-10"},{"project_name":"魔镜","creator":"吕永生","create_date":"2017-03-10"},{"project_name":"排","creator":"吕永生","create_date":"2017-03-10"}]},{"adate":"2017-03-09","customer_id":26934,"org_id":2,"customer_name":"丁文红","avator":null,"guid":"customer_1_26934","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%96%87%E7%BA%A2","schedule_list":[{"schedule_id":106588,"org_id":2,"status":2,"start_time":"9:30","end_time":"11:00","engineer_id":"67","project_name":"培元固本","status_text":"进行中","engineer_name":"李庆洁"}],"plan_list":null},{"adate":"2017-03-08","customer_id":28115,"org_id":2,"customer_name":"丁伟","avator":null,"guid":"customer_1_28115","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%81%E4%BC%9F","schedule_list":[{"schedule_id":106584,"org_id":2,"status":2,"start_time":"12:00","end_time":"13:30","engineer_id":"68","project_name":"水养","status_text":"进行中","engineer_name":"朱健荣"}],"plan_list":[{"project_name":"水养","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"魔镜","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"脱毒","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"种植","creator":"zhangdanfeng","create_date":"2017-03-12"}]},{"adate":"2017-03-08","customer_id":28222,"org_id":2,"customer_name":"戴桂川","avator":null,"guid":"customer_1_28222","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%A1%82%E5%B7%9D","schedule_list":[{"schedule_id":106587,"org_id":2,"status":2,"start_time":"19:00","end_time":"20:30","engineer_id":"69","project_name":"排","status_text":"进行中","engineer_name":"和珍"},{"schedule_id":106585,"org_id":2,"status":2,"start_time":"16:30","end_time":"18:00","engineer_id":"68","project_name":"表皮","status_text":"进行中","engineer_name":"朱健荣"},{"schedule_id":106586,"org_id":2,"status":2,"start_time":"15:00","end_time":"16:30","engineer_id":"69","project_name":"排","status_text":"进行中","engineer_name":"和珍"}],"plan_list":null},{"adate":"2017-03-06","customer_id":26497,"org_id":1,"customer_name":"潘佳","avator":null,"guid":"customer_1_26497","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%BD%98%E4%BD%B3","schedule_list":[{"schedule_id":106528,"org_id":1,"status":0,"start_time":"12:30","end_time":"13:00","engineer_id":"249","project_name":"纤体能量仪","status_text":"未开始","engineer_name":"吕永生"}],"plan_list":null}],"pageSize":10,"pageNumber":1,"totalPage":99}
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
         * total : 986
         * rows : [{"adate":"2017-03-16","customer_id":28004,"org_id":2,"customer_name":"汤丹燕","avator":null,"guid":"customer_1_28004","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%B9%E7%87%95","schedule_list":[{"schedule_id":106606,"org_id":2,"status":0,"start_time":"13:30","end_time":"15:00","engineer_id":"72","project_name":"水通道护理","status_text":"未开始","engineer_name":"郭周雨"}],"plan_list":null},{"adate":"2017-03-16","customer_id":28049,"org_id":2,"customer_name":"富静艳","avator":null,"guid":"customer_1_28049","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E9%9D%99%E8%89%B3","schedule_list":[{"schedule_id":106605,"org_id":2,"status":0,"start_time":"14:00","end_time":"15:30","engineer_id":"73","project_name":"水养","status_text":"未开始","engineer_name":"潘慧"},{"schedule_id":106604,"org_id":2,"status":0,"start_time":"11:30","end_time":"13:00","engineer_id":"69","project_name":"水通道护理","status_text":"未开始","engineer_name":"和珍"}],"plan_list":null},{"adate":"2017-03-15","customer_id":26514,"org_id":2,"customer_name":"虞志萍VIP","avator":null,"guid":"customer_1_26514","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E8%90%8DV","schedule_list":[{"schedule_id":106603,"org_id":2,"status":0,"start_time":"12:00","end_time":"13:00","engineer_id":"128","project_name":"哈尼药浴*4","status_text":"未开始","engineer_name":"包雪梅"},{"schedule_id":106602,"org_id":2,"status":0,"start_time":"10:30","end_time":"12:00","engineer_id":"73","project_name":"排","status_text":"未开始","engineer_name":"潘慧"}],"plan_list":[{"project_name":"哈尼药浴*5","creator":"zhangdanfeng","create_date":"2017-03-13"},{"project_name":"哈尼药浴*6","creator":"zhangdanfeng","create_date":"2017-03-13"},{"project_name":"纤体能量仪","creator":"zhangdanfeng","create_date":"2017-03-13"}]},{"adate":"2017-03-15","customer_id":27993,"org_id":2,"customer_name":"王奇","avator":null,"guid":"customer_1_27993","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E7%8E%8B%E5%A5%87","schedule_list":[{"schedule_id":106600,"org_id":2,"status":0,"start_time":"19:30","end_time":"20:30","engineer_id":"46","project_name":"哈尼药浴*2","status_text":"未开始","engineer_name":"刘仙"},{"schedule_id":106601,"org_id":2,"status":0,"start_time":"17:00","end_time":"18:00","engineer_id":"128","project_name":"哈尼药浴*1","status_text":"未开始","engineer_name":"包雪梅"},{"schedule_id":106598,"org_id":2,"status":0,"start_time":"12:30","end_time":"13:00","engineer_id":"67","project_name":"肌肉放松","status_text":"未开始","engineer_name":"李庆洁"}],"plan_list":null},{"adate":"2017-03-12","customer_id":28115,"org_id":2,"customer_name":"丁伟","avator":null,"guid":"customer_1_28115","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%81%E4%BC%9F","schedule_list":[{"schedule_id":106593,"org_id":2,"status":0,"start_time":"17:30","end_time":"18:00","engineer_id":"67","project_name":"魔镜","status_text":"未开始","engineer_name":"李庆洁"}],"plan_list":[{"project_name":"水养","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"魔镜","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"脱毒","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"种植","creator":"zhangdanfeng","create_date":"2017-03-12"}]},{"adate":"2017-03-10","customer_id":28076,"org_id":2,"customer_name":"金岚","avator":null,"guid":"customer_1_28076","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E9%87%91%E5%B2%9A","schedule_list":[{"schedule_id":106589,"org_id":2,"status":0,"start_time":"11:00","end_time":"12:30","engineer_id":"185","project_name":"平衡霜*1","status_text":"未开始","engineer_name":"李金兰"}],"plan_list":[{"project_name":"天地藏浴","creator":"吕永生","create_date":"2017-03-10"},{"project_name":"魔镜","creator":"吕永生","create_date":"2017-03-10"},{"project_name":"排","creator":"吕永生","create_date":"2017-03-10"}]},{"adate":"2017-03-09","customer_id":26934,"org_id":2,"customer_name":"丁文红","avator":null,"guid":"customer_1_26934","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%96%87%E7%BA%A2","schedule_list":[{"schedule_id":106588,"org_id":2,"status":2,"start_time":"9:30","end_time":"11:00","engineer_id":"67","project_name":"培元固本","status_text":"进行中","engineer_name":"李庆洁"}],"plan_list":null},{"adate":"2017-03-08","customer_id":28115,"org_id":2,"customer_name":"丁伟","avator":null,"guid":"customer_1_28115","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%81%E4%BC%9F","schedule_list":[{"schedule_id":106584,"org_id":2,"status":2,"start_time":"12:00","end_time":"13:30","engineer_id":"68","project_name":"水养","status_text":"进行中","engineer_name":"朱健荣"}],"plan_list":[{"project_name":"水养","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"魔镜","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"脱毒","creator":"zhangdanfeng","create_date":"2017-03-12"},{"project_name":"种植","creator":"zhangdanfeng","create_date":"2017-03-12"}]},{"adate":"2017-03-08","customer_id":28222,"org_id":2,"customer_name":"戴桂川","avator":null,"guid":"customer_1_28222","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%A1%82%E5%B7%9D","schedule_list":[{"schedule_id":106587,"org_id":2,"status":2,"start_time":"19:00","end_time":"20:30","engineer_id":"69","project_name":"排","status_text":"进行中","engineer_name":"和珍"},{"schedule_id":106585,"org_id":2,"status":2,"start_time":"16:30","end_time":"18:00","engineer_id":"68","project_name":"表皮","status_text":"进行中","engineer_name":"朱健荣"},{"schedule_id":106586,"org_id":2,"status":2,"start_time":"15:00","end_time":"16:30","engineer_id":"69","project_name":"排","status_text":"进行中","engineer_name":"和珍"}],"plan_list":null},{"adate":"2017-03-06","customer_id":26497,"org_id":1,"customer_name":"潘佳","avator":null,"guid":"customer_1_26497","avator_url":"http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E6%BD%98%E4%BD%B3","schedule_list":[{"schedule_id":106528,"org_id":1,"status":0,"start_time":"12:30","end_time":"13:00","engineer_id":"249","project_name":"纤体能量仪","status_text":"未开始","engineer_name":"吕永生"}],"plan_list":null}]
         * pageSize : 10
         * pageNumber : 1
         * totalPage : 99
         */

        private int total;
        private int pageSize;
        private int pageNumber;
        private int totalPage;
        private List<RowsBean> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {
            /**
             * adate : 2017-03-16
             * customer_id : 28004
             * org_id : 2
             * customer_name : 汤丹燕
             * avator : null
             * guid : customer_1_28004
             * avator_url : http://mybook.sosys.cn:8080/php/get.php?act=icon&name=%E4%B8%B9%E7%87%95
             * schedule_list : [{"schedule_id":106606,"org_id":2,"status":0,"start_time":"13:30","end_time":"15:00","engineer_id":"72","project_name":"水通道护理","status_text":"未开始","engineer_name":"郭周雨"}]
             * plan_list : null
             */

            private String adate;
            private int customer_id;
            private int org_id;
            private String customer_name;
            private String avator;
            private String guid;
            private String avator_url;
            private List<PlanListBean> plan_list;
            private List<ScheduleListBean> schedule_list;
            private int plan_project_total;
            private int plan_product_total;

            public int getPlan_project_total() {
                return plan_project_total;
            }

            public void setPlan_project_total(int plan_project_total) {
                this.plan_project_total = plan_project_total;
            }

            public int getPlan_product_total() {
                return plan_product_total;
            }

            public void setPlan_product_total(int plan_product_total) {
                this.plan_product_total = plan_product_total;
            }

            public String getAdate() {
                return adate;
            }

            public void setAdate(String adate) {
                this.adate = adate;
            }

            public int getCustomer_id() {
                return customer_id;
            }

            public void setCustomer_id(int customer_id) {
                this.customer_id = customer_id;
            }

            public int getOrg_id() {
                return org_id;
            }

            public void setOrg_id(int org_id) {
                this.org_id = org_id;
            }

            public String getCustomer_name() {
                return customer_name;
            }

            public void setCustomer_name(String customer_name) {
                this.customer_name = customer_name;
            }

            public String getAvator() {
                return avator;
            }

            public void setAvator(String avator) {
                this.avator = avator;
            }

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            public String getAvator_url() {
                return avator_url;
            }

            public void setAvator_url(String avator_url) {
                this.avator_url = avator_url;
            }

            public List<PlanListBean> getPlan_list() {
                return plan_list;
            }

            public void setPlan_list(List<PlanListBean> plan_list) {
                this.plan_list = plan_list;
            }

            public List<ScheduleListBean> getSchedule_list() {
                return schedule_list;
            }

            public void setSchedule_list(List<ScheduleListBean> schedule_list) {
                this.schedule_list = schedule_list;
            }

            public static class ScheduleListBean {
                /**
                 * schedule_id : 106606
                 * org_id : 2
                 * status : 0
                 * start_time : 13:30
                 * end_time : 15:00
                 * engineer_id : 72
                 * project_name : 水通道护理
                 * status_text : 未开始
                 * engineer_name : 郭周雨
                 */

                private int schedule_id;
                private int org_id;
                private int status;
                private String start_time;
                private String end_time;
                private String engineer_id;
                private String project_name;
                private String status_text;
                private String engineer_name;

                public int getSchedule_id() {
                    return schedule_id;
                }

                public void setSchedule_id(int schedule_id) {
                    this.schedule_id = schedule_id;
                }

                public int getOrg_id() {
                    return org_id;
                }

                public void setOrg_id(int org_id) {
                    this.org_id = org_id;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getStart_time() {
                    return start_time;
                }

                public void setStart_time(String start_time) {
                    this.start_time = start_time;
                }

                public String getEnd_time() {
                    return end_time;
                }

                public void setEnd_time(String end_time) {
                    this.end_time = end_time;
                }

                public String getEngineer_id() {
                    return engineer_id;
                }

                public void setEngineer_id(String engineer_id) {
                    this.engineer_id = engineer_id;
                }

                public String getProject_name() {
                    return project_name;
                }

                public void setProject_name(String project_name) {
                    this.project_name = project_name;
                }

                public String getStatus_text() {
                    return status_text;
                }

                public void setStatus_text(String status_text) {
                    this.status_text = status_text;
                }

                public String getEngineer_name() {
                    return engineer_name;
                }

                public void setEngineer_name(String engineer_name) {
                    this.engineer_name = engineer_name;
                }
            }

            public static class PlanListBean {
                /**
                 * project_name : 哈尼药浴*5
                 * creator : zhangdanfeng
                 * create_date : 2017-03-13
                 */
                private int project_id;
                private String project_name;
                private String creator;
                private String create_date;
                private int status;

                public int getProject_id() {
                    return project_id;
                }

                public void setProject_id(int project_id) {
                    this.project_id = project_id;
                }

                public int getStatus() {
                    return status;
                }

                public void setStatus(int status) {
                    this.status = status;
                }

                public String getProject_name() {
                    return project_name;
                }

                public void setProject_name(String project_name) {
                    this.project_name = project_name;
                }

                public String getCreator() {
                    return creator;
                }

                public void setCreator(String creator) {
                    this.creator = creator;
                }

                public String getCreate_date() {
                    return create_date;
                }

                public void setCreate_date(String create_date) {
                    this.create_date = create_date;
                }
            }
        }
    }
}
