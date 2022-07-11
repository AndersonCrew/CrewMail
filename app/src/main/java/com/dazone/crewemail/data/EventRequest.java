package com.dazone.crewemail.data;

import java.util.List;

/**
 * Created by dazone on 6/1/2017.
 */

public class EventRequest {
    private List<PersonData> datas;
    private int type;

    public EventRequest() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<PersonData> getDatas() {
        return datas;
    }

    public void setDatas(List<PersonData> datas) {
        this.datas = datas;
    }
}
