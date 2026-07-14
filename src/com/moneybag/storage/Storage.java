package com.moneybag.storage;

import com.moneybag.model.Transaction;
import java.io.IOException;
import java.util.List;

/**
 * định nghĩa các hành vi lưu trữ dữ liệu chung
 */
public interface Storage {
    // lưu danh sách giao dịch vào một đươờng dẫn file
    void save(List<Transaction> transactions, String filePath) throws IOException;

    // tải danh sách giao dịch từ một đường dẫn file lên hệ thống
    List<Transaction> load(String filePath) throws IOException;
}
