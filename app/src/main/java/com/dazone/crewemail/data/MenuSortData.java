package com.dazone.crewemail.data;

/**
 * Created by THANHTUNG on 28/01/2016.
 */
public class MenuSortData {
    private String nameSort;
    private int isCheck;
    private int sort;

    public MenuSortData(String nameSort, int isCheck, int sort) {
        this.nameSort = nameSort;
        this.isCheck = isCheck;
        this.sort = sort;
    }

    public String getNameSort() {
        return nameSort;
    }

    public void setNameSort(String nameSort) {
        this.nameSort = nameSort;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
