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
        // CỰC KỲ QUAN TRỌNG: Mở FileWriter KHÔNG có tham số true để nó xóa sạch file cũ trước khi ghi mới
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // Ghi dòng tiêu đề chuẩn, không chứa dấu ngoặc kép bọc ngoài
            writer.write("id,type,amount,date,note,category,wallet,extraInfo");
            writer.newLine();

            for (Transaction t : transactions) {
                String type = t.getType().toString();
                String extraInfo = "";

                if (t instanceof Income) {
                    extraInfo = ((Income) t).getSource();
                } else if (t instanceof Expense) {
                    extraInfo = ((Expense) t).getPaymentMethod();
                }

                // Ghi chuỗi thuần túy, phân tách bằng dấu phẩy
                String line = String.format("%s,%s,%.0f,%s,%s,%s,%s,%s",
                        t.getId(),
                        type,
                        t.getAmount(),
                        t.getDate(),
                        t.getNote(),
                        t.getCategory().getName(),
                        t.getWallet().getName(),
                        extraInfo
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    @Override
    public List<Transaction> load(String filePath) throws IOException {
        List<Transaction> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return list; // trả về null nếu chưa tồn tại/ lần đầu chạy
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // bỏ qua tiêu đề

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // cắt chuỗi bằng dầu chấm phẩy
                if (parts.length >=8 ){
                    try {
                        String id = parts[0];
                        com.moneybag.constant.TransactionType type = com.moneybag.constant.TransactionType.valueOf(parts[1]);
                        double amount = Double.parseDouble(parts[2]);
                        java.time.LocalDate date = java.time.LocalDate.parse(parts[3]);
                        String note = parts[4];

                        // tái tạo category và wallet
                        com.moneybag.model.Category cat = new com.moneybag.model.Category(parts[5], type );
                        com.moneybag.wallet.Wallet wal = new com.moneybag.wallet.CashWallet(parts[6], 0);
                        String extraInfo = parts[7];

                        // dùng factory để đống gói đối tượng
                        com.moneybag.model.Transaction t = com.moneybag.factory.TransactionFactory.createTransaction(
                               type, amount, date, note, cat, wal, extraInfo, null
                        );
                        t.setId(id); // ghi đè lại id cũ từ file

                        list.add(t);
                    } catch (Exception e) {
                        System.out.println("⚠️ Bỏ qua dòng dữ liệu lỗi trong CSV: " + line);
                    }
                }
            }
        }
        return list;
    }
}
