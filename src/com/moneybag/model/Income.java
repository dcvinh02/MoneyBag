package com.moneybag.model;

import com.moneybag.constant.TransactionType;
import com.moneybag.wallet.Wallet;
import java.time.LocalDate;

/**
 * Khoản thu: Kế thừa từ Transaction
 */

public class Income extends Transaction {
    private String source; //Nguồn thu

    public Income(String id, double amount, LocalDate date, String note, Category category, Wallet wallet, String source) {
        super(id, amount, date, note, category, wallet);
        // dùng super để gọi constructor của lớp cha (Transaction)
        this.source = source;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }

    @Override
    public double getSignedAmount() {
        return this.getAmount(); // khoản thu thì số tiền mang dấu dương +
    }

    @Override
    public void printInfo() {
        System.out.printf("[THU] Ngày: %s | Nguồn: %s | Danh mục: %s | Số tiền: +%,.0f VNĐ | Ví: %s\n",
                this.getDate(), this.source, this.getCategory().getName(), this.getAmount(), this.getWallet().getName());
    }

    public String getSource() { return source; }
}
