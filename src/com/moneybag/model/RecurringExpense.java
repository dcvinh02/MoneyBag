package com.moneybag.model;

import com.moneybag.constant.Period;
import com.moneybag.wallet.Wallet;
import java.time.LocalDateTime;

/**
 * Khoản chi định kỳ: kế thừa từ expense
 */
public class RecurringExpense extends Expense {
    private Period period; //Chu kì thanh toán định kỳ

    public RecurringExpense(String id, double amount, LocalDateTime date, String note, Category category, Wallet wallet, String paymentMethod, Period period) {
        // Gọi lại constructor của lớp cha là Expense
        super(id, amount, date, note, category, wallet, paymentMethod);
        this.period = period;
    }

    /**
     * Tính toán ngày gia hạn kế tiếp dựa trên chu kì đã chọn
     */
    public LocalDateTime nextDueDate() {
        LocalDateTime current = this.getDate();
        if (period == null) return current;

        // cấu trúc swich-case để xử lý đa dạng các chu kì
        switch (period) {
            case DAILY:
                return current.plusDays(1);
            case WEEKLY:
                return current.plusWeeks(1);
            case MONTHLY:
                return current.plusMonths(1);
            case YEARLY:
                return current.plusYears(1);
            default:
                return current;
        }
    }
    @Override
    public void printInfo() {
        System.out.printf("[ĐỊNH KỲ - %s] Ngày bắt đầu: %s | Danh mục: %s | Số tiền: -%,.0f VNĐ | Kỳ tới: %s\n",
                this.period, this.getDate(), this.getCategory().getName(), this.getAmount(), this.nextDueDate());
    }

    public Period getPeriod() { return period; }
}
