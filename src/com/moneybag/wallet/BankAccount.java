package com.moneybag.wallet;

import com.moneybag.constant.WalletType;

/**
 * Tài khoản ngân hàng: kế thừa wallet, bổ sung số tài khoản và tên ngân hàng
 */
public class BankAccount extends Wallet{
    private String bankName;
    private String accountNumber;

    // Giả định ngân hàng thu phí 0.1% mỗi lần rút tiền (để thể hiện tính đa hình khác biệt)
    private static final double TRANSACTION_FEE_PERCENT = 0.001;

    public BankAccount(String name, double balance, String bankName, String accountNumber) {
        super(name, balance);
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }

    @Override
    public void withdraw(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Số tiền rút không được âm!");
        }

        double fee = amount * TRANSACTION_FEE_PERCENT;
        double totalDeduction = amount + fee; // tổng số tiền thực tế bị trừ

        if (this.getBalance() < totalDeduction) {
            throw new IllegalStateException("Tài koản ngân hàng không đủ số dư để thanh toán!");
        }

        this.setBalance(this.getBalance() - totalDeduction);
    }
    @Override
    public WalletType getWalletType() {
        return WalletType.BANK;
    }

    public String getBankName() {
        return bankName;
    }
    public String getAccountNumber(){
        return accountNumber;
    }
}
