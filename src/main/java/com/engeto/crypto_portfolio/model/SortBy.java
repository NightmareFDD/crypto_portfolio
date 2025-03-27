package com.engeto.crypto_portfolio.model;

public enum SortBy {
    NAME, PRICE, QUANTITY;

    public static SortBy fromString(String value) {
        try{
            return SortBy.valueOf(value.toUpperCase());
        } catch (Exception e){
            return null;
        }
    }
}
