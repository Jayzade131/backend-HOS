package com.org.hosply360.dto.OPDDTO;

import java.util.List;

public class PagedResult<T> {
    private List<T> data;
    private long total;

    public PagedResult(List<T> data, long total) {
        this.data = data;
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public long getTotal() {
        return total;
    }
}