package com.info.qrcodecamera;

import java.util.List;

public class DecodedQRRecord {

    private List<DecodedQRRecord> records;
    private String id;
    private long date;

    public List<DecodedQRRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DecodedQRRecord> records) {
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
