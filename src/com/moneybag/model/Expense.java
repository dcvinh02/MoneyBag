package com.moneybag.model;

import com.moneybag.constant.TransactionType;
import com.moneybag.wallet.Wallet;
import java.time.LocalDateTime;

/**
 * Khoản chi: kế thừa Transaction
 */

public class Expense extends Transaction {
    private String paymentMethod; // phương thức thanh toán ( quẹt thẻ, tiền mặt )

    public Expense(String id, double amount, LocalDateTime date, String note, Category category, Wallet wallet, String paymentMethod) {
        super(id, amount, date, note, category, wallet);
        this.paymentMethod = paymentMethod;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }

    @Override
    public double getSignedAmount() {
        return -this.getAmount(); // khoản chi thì mang dấu âm -
    }

    @Override
    public void printInfo() {
        System.out.printf("[CHI] Ngày: %s | Ghi chú: %s | Danh mục: %s | Số tiền: -%,.0f VNĐ | Ví: %s\n",
                this.getDate(), this.getNote(), this.getCategory().getName(), this.getAmount(), this.getWallet().getName());
    }
    public String getPaymentMethod() { return paymentMethod; }
}
