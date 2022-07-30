package com.example.kakeibo.model;

import java.util.regex.Pattern;

public class Purchase {
    private Integer purchaseID;
    private String purchase, purchaseCategory, purchaseDate;
    private Double purchaseAmount;


    public Purchase() {

    }

    public String getPurchase() {
        return purchase;
    }

    public void setPurchase(String purchase) {
        this.purchase = purchase;
    }

    public String getPurchaseCategory() {
        return purchaseCategory;
    }

    public void setPurchaseCategory(String purchaseCategory) {
        this.purchaseCategory = purchaseCategory;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Double getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(Double purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public static boolean isValidDateOfBirth(String dateOfBirth) {
        {
            String regex = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
            Pattern pattern = Pattern.compile(regex);
            if (dateOfBirth == null)
                return false;
            return pattern.matcher(dateOfBirth).matches();
        }
    }

    public static boolean isValidAmount(String purchaseAmount) {
        {
            String regex = "[0-9]+([,.][0-9]{1,2})?";
            Pattern pattern = Pattern.compile(regex);
            if (purchaseAmount == null)
                return false;
            return pattern.matcher(purchaseAmount).matches();
        }


    }
}


