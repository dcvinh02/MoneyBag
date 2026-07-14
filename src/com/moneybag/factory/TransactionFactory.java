package com.moneybag.factory;

import com.moneybag.constant.Period;
import com.moneybag.constant.TransactionType;
import com.moneybag.model.*;
import com.moneybag.wallet.Wallet;
import java.time.LocalDate;
import java.util.UUID; // thư viện tạo ID

/**
 * khỏi tạo các đối tượng transaction phức tạp
 */
public class TransactionFactory {
    // phương thức tính static khởi tạo giao dịch mà không cần khởi tạo Fatory
    public static Transaction createTransaction(
            TransactionType type,
            double amount,
            LocalDate date,
            String note,
            Category category,
            Wallet wallet,
            String extraInfo,
            Period period // truyền vào chu kỳ nếu là khoản phí chi định kì, nếu không truyền thì null
    ) {
        // tự dộng sinh ra 1 ID random và duy nhất
        String id = UUID.randomUUID().toString();
        // dựa vào loại giao dịch (type) để quyết định sẽ gọi Constructor của Class nào
        switch (type) {
            case INCOME:
                return new Income(id, amount, date, note, category, wallet, extraInfo);

                 case EXPENSE:
                     // nếu truyền vào chu ký (period != null) thì tạo ra RecurringExpense
                     if (period != null) {
                         return new RecurringExpense(id, amount, date, note, category, wallet, extraInfo, period);
                     }
                     // nếu không có chu kỳ, tạo Expense bình thường
                     return new Expense(id, amount, date, note, category, wallet, extraInfo);
                     default:
                         throw new IllegalArgumentException("Loại giao dịch không được hệ thống hỗ trợ!");
        }
    }
}
