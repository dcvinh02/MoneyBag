package com.moneybag.wallet;

import com.moneybag.constant.WalletType;

/**
 * Lớp trừu tượng đại diện cho một nơi lưu trữ tiền chung.
 */
public abstract class Wallet {
    private String name;
    private double balance; // Số dư

    public Wallet(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    // Các hành vi buộc lớp con phải tự định nghĩa (Đa hình)
    public abstract void withdraw(double amount);
    public abstract WalletType getWalletType();

    // Hành vi chung: Nạp tiền (Ví nào cũng nạp tiền giống nhau là cộng thêm vào số dư)
    public void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền nạp không được âm!");
        }
        this.balance += amount;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }

    // Protected để chỉ các lớp con (ví dụ BankAccount khi tính phí) mới được phép trừ thẳng số dư
    protected void setBalance(double balance) {
        this.balance = balance;
    }
}