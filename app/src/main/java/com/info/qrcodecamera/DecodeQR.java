package com.info.qrcodecamera;

import android.content.Context;

import com.tunabaranurut.microdb.base.MicroDB;

public class DecodeQR {

    private String uniqueCode;
    private String companyCode;
    private String productCode;
    private String numberofPiece;
    private String staticCode;
    public MicroDB microDB;
    public DecodeQR decodeQR;
    public MainActivity mainActivity;

public void saveDecodeQRCodes(){

    microDB= new MicroDB(mainActivity.getApplicationContext()); // parantez içi ?????

    try {
        microDB.save("uniqueCode"   ,decodeQR.getUniqueCode());
        microDB.save("companyCode"  ,decodeQR.getCompanyCode());
        microDB.save("productCode"  ,decodeQR.getProductCode());
        microDB.save("numberofPiece",decodeQR.getNumberofPiece());
        microDB.save("staticCode"   ,decodeQR.getStaticCode());

    } catch (Exception e) {
        e.printStackTrace();
    }

}

    public void DecodeQR(){}

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
}
