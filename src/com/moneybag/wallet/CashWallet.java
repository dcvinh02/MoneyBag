package com.moneybag.wallet;

import com.moneybag.constant.WalletType:

/**
 * Ví tiền mặt: kế thừa wallet
 */
public class CashWallet extends Wallet {
    public CashWallet(String name, double balance) {
        super(name, balance);
    }
    @Override
    public void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền rút không được âm!");
        }
        // kiểm tra số dư có đủ để rút không
        if (this.getBalance() < amount) {
            throw new IllegalArgumentException("Tài khoản tiền mặt không đủ số dư!");
        }
        // trừ trực tiếp vào số dư
        this.setBalance(this.getBalance() - amount);
    }

    @Override
    public WalletType getWalletType() {
        return WalletType.CASH;
    }
}
