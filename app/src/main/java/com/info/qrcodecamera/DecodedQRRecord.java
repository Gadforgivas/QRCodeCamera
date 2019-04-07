package com.info.qrcodecamera;

import java.util.LinkedList;
import java.util.List;

public class DecodedQRRecord {

    private List<DecodedQR> records = new LinkedList<>();
    private String id;
    private long date;

    public List<DecodedQR> getRecords() {
        return records;
    }

    public void setRecords(List<DecodedQR> records) {
        this.records = records;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
