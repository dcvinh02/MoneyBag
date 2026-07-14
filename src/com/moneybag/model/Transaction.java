package com.moneybag.model;

import com.moneybag.constant.TransactionType;
import com.moneybag.wallet.Wallet;
import java.time.LocalDate;

/**
 * lớp đại diện cho một giao dịch tài chính chung
 */
public abstract class Transaction {
    private String id;
    private double amount;
    private LocalDate date;
    private String note;
    private Category category;
    private Wallet wallet;

    // constructor
    public Transaction(String id, double amount, LocalDate date, String note, Category category, Wallet wallet) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.wallet = wallet;
    }

    // các phương thức buộc lớp con phải tự định nghĩa
    public abstract TransactionType getType();
    public abstract double getSignedAmount();
    public abstract void printInfo();

    // -getter và Setter để truy cập dữ liệu
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }

    // kiểm tra tính hợp lệ của dữ liệu đầu vào
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền không được âm!");
        }
        this.amount = amount;
    }

    public LocalDate getDate() { return date; }
    public String getNote() { return note; }
    public Category getCategory() { return category; }
    public Wallet getWallet() { return wallet; }

    // phục hồi tham chếu Ví từ file CSV
    public void setWallet(Wallet wallet) { this.wallet = wallet; }
}