package com.moneybag.wallet;

import com.moneybag.constant.WalletType;

/**
 * Lưu trữ tiền chung.
 */
public abstract class Wallet {
    private String name;
    private double balance; // Số dư

    public Wallet(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public abstract void withdraw(double amount);
    public abstract WalletType getWalletType();

    // nạp tiền
    public void deposit(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền nạp không được âm!");
        }
        this.balance += amount;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }

    protected void setBalance(double balance) {
        this.balance = balance;
    }
}