package com.lazai.entity.dto;

import java.util.List;

public class CommonPageResult<T> {

    private List<T> items;

    private CommonPageDTO pagination;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public CommonPageDTO getPagination() {
        return pagination;
    }

    public void setPagination(CommonPageDTO pagination) {
        this.pagination = pagination;
    }
}
