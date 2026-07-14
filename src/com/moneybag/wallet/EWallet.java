package com.moneybag.wallet;

import com.moneybag.constant.WalletType;

/**
 * ví điện tử: kế thừa wallet, thêm nhà cung cấp
 */
public class EWallet extends Wallet {
    private String provider; // ví dụ: momo, zalopay, shopeepay

    public EWallet(String name, double balance, String provider){
        super(name, balance);
        this.provider=provider;
    }

    @Override
    public void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền rút không được âm!");
        }
        if (this.getBalance() < amount) {
            throw new IllegalStateException("Ví điện tử " + provider + "không đủ số dư!");
        }
        this.setBalance(this.getBalance() - amount);
    }

    @Override
    public WalletType getWalletType() {
        return WalletType.EWALLET;
    }

    public  String getProvider() {
        return provider;
    }
}
