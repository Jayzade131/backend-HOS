package com.org.hosply360.dto.pathologyDTO;


import java.util.List;

public class PagedResultForTest<T> {
    private List<T> data;
    private long total;
    private int page;
    private int size;

    public PagedResultForTest(List<T> data, long total, int page, int size) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public List<T> getData() {
        return data;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}


