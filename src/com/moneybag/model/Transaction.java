package com.moneybag.model;

import com.moneybag.constant.TransactionType;
import com.moneybag.wallet.Wallet;
import java.time.LocalDate;

/**
 * Lớp trừu tượng đại diện cho một giao dịch tài chính chung.
 * Thể hiện tính Trừu tượng (Abstraction) trong OOP[cite: 12, 119].
 */
public abstract class Transaction {
    // Đóng gói (Encapsulation): Các thuộc tính để private để bảo vệ dữ liệu [cite: 12, 115]
    private String id;
    private double amount;
    private LocalDate date;
    private String note;
    private Category category;
    private Wallet wallet;

    // Constructor để các lớp con gọi thông qua super()
    public Transaction(String id, double amount, LocalDate date, String note, Category category, Wallet wallet) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.wallet = wallet;
    }

    // --- Các phương thức trừu tượng buộc lớp con phải tự định nghĩa (Tính Đa hình) [cite: 117, 119] ---
    public abstract TransactionType getType();
    public abstract double getSignedAmount();
    public abstract void printInfo();

    // --- Getter và Setter để truy cập dữ liệu an toàn (Đóng gói) [cite: 115] ---
    public String getId() { return id; }
    public double getAmount() { return amount; }

    // Setter có kiểm tra tính hợp lệ của dữ liệu đầu vào [cite: 45, 115]
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
}