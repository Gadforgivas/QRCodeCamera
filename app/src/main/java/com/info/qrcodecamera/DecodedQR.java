package com.info.qrcodecamera;

import android.content.Context;

import com.tunabaranurut.microdb.base.MicroDB;

public class DecodedQR {

    private long timestamp;
    private String uniqueCode;
    private String companyCode;
    private String productCode;
    private String numberofPiece;
    private String staticCode;
    private String rawCode;

    public void DecodeQR(){}

//    public void saveDecodeQRCodes(Context context){
//
//        microDB= new MicroDB(context); // parantez içi ?????
//
//        try {
//            microDB.save("uniqueCode"   , this.getUniqueCode());
//            microDB.save("companyCode"  , this.getCompanyCode());
//            microDB.save("productCode"  , this.getProductCode());
//            microDB.save("numberofPiece", this.getNumberofPiece());
//            microDB.save("staticCode"   , this.getStaticCode());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) { this.uniqueCode = uniqueCode; }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getNumberofPiece() {
        return numberofPiece;
    }

    public void setNumberofPiece(String numberofPiece) {
        this.numberofPiece = numberofPiece;
    }

    public String getStaticCode() {
        return staticCode;
    }

    public void setStaticCode(String staticCode) {
        this.staticCode = staticCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRawCode() {
        return rawCode;
    }

    public void setRawCode(String rawCode) {
        this.rawCode = rawCode;
    }
}
