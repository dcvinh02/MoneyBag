package com.moneybag.model;

import com.moneybag.constant.Period;

/**
 * lớp đại diện cho ngân sách dự kiến của 1 danh mục c thể
 */
public class Budget {
    private Category category;
    private double limit; // hạn mức tối đa được chi
    private Period period; // đặt hàng tháng

    public Budget(Category category, double limit, Period period) {
        this.category = category;
        this.limit = limit;
        this.period = period;
    }

    /**
     * Kiểm tra xem tổng số tiền tiêu có vượt mức không
     *
     */
    public boolean isExeeded(double spent) { // tham số spent số tiền đã tiêu trong kì
        return spent > limit;
    }

    public Category getCategory() { return category; }
    public double getLimit() { return limit; }
    public Period getPeriod() { return period; }
}
