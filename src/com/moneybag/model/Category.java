package com.moneybag.model;

import com.moneybag.constant.TransactionType;

/**
 * Đại diện cho mục thu/chi
 */
public class Category {
    private String name;
    private TransactionType type;

    public Category(String name, TransactionType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public TransactionType getType() { return type; }
}
