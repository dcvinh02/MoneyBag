package com.moneybag.storage;

import com.moneybag.model.Transaction;
import com.moneybag.model.Income;
import com.moneybag.model.Expense;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * cải đặt lưu trữ dạng csv
 */
public class CsvStorage implements Storage {

    @Override
    public void save(List<Transaction> transactions, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("id,type,amount,date,note,catagory,wallet,extraInfo");
            writer.newLine();

            for (Transaction t : transactions) {
                String type = t.getType().toString();
                String extraInfo = "";

                if (t instanceof Income) {
                    extraInfo = ((Income) t).getSource();
                } else if (t instanceof Expense) {
                    extraInfo = ((Expense) t).getPaymentMethod();
                }

                String line = String.format("\"%s,%s,%.0f,%s,%s,%s,%s,%s",
                        t.getId(), type, t.getAmount(), t.getDate(),
                        t.getNote(), t.getCategory().getName(),
                        t.getWallet().getName(), extraInfo);
                writer.write(line);
                writer.newLine();
            }
        }
    }

    @Override
    public List<Transaction> load(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        // tạm thời trả về danh sách rỗng, hoàn thiện logic đọc sau
        return new ArrayList<>();
    }
}
