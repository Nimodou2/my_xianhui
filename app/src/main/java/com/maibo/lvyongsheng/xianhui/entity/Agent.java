package com.maibo.lvyongsheng.xianhui.entity;

/**
 * Created by LYS on 2016/9/28.
 */
public class Agent {
    private String agent_id;
    private String agent_name;
    private String is_default;

    public Agent(String agent_id, String agent_name, String is_default) {
        this.agent_id = agent_id;
        this.agent_name = agent_name;
        this.is_default = is_default;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_name() {
        return agent_name;
    }

    public void setAgent_name(String agent_name) {
        this.agent_name = agent_name;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
