package com.moneybag.gui;

import com.moneybag.service.ExpenseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private ExpenseManager manager;

    // Khai báo các thành phần giao diện ở mức Class để các hàm khác có thể gọi tới
    private JLabel lblTotalIncome, lblTotalExpense, lblBalance;
    private JTable transactionTable;
    private DefaultTableModel tableModel;

    public MainFrame() {
        this.manager = ExpenseManager.getInstance();
        manager.loadData();

        setTitle("MoneyBag - Quản Lý Chi Tiêu Cá Nhân");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.saveData();
                System.exit(0);
            }
        });

        // ==========================================
        // 1. VÙNG PHÍA BẮC (NORTH) - DASHBOARD
        // ==========================================
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topPanel.setBorder(BorderFactory.createTitledBorder("Tổng Quan Giao Dịch"));

        lblTotalIncome = new JLabel("Tổng Thu: 0 VNĐ", SwingConstants.CENTER);
        lblTotalIncome.setForeground(new Color(0, 150, 0)); // Màu xanh lá
        lblTotalIncome.setFont(new Font("Arial", Font.BOLD, 16));

        lblTotalExpense = new JLabel("Tổng Chi: 0 VNĐ", SwingConstants.CENTER);
        lblTotalExpense.setForeground(Color.RED); // Màu đỏ
        lblTotalExpense.setFont(new Font("Arial", Font.BOLD, 16));

        lblBalance = new JLabel("Số Dư: 0 VNĐ", SwingConstants.CENTER);
        lblBalance.setForeground(Color.BLUE); // Màu xanh dương
        lblBalance.setFont(new Font("Arial", Font.BOLD, 16));

        topPanel.add(lblTotalIncome);
        topPanel.add(lblTotalExpense);
        topPanel.add(lblBalance);
        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. VÙNG TRUNG TÂM (CENTER) - BẢNG DỮ LIỆU
        // ==========================================
        // Tạo các cột cho bảng
        String[] columnNames = {"Ngày", "Loại", "Danh Mục", "Số Tiền", "Ví", "Ghi Chú"};
        // DefaultTableModel giúp chúng ta dễ dàng thêm/xóa dữ liệu vào bảng sau này
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);

        // Bọc bảng trong một JScrollPane để có thanh cuộn nếu dữ liệu quá dài
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lịch Sử Chi Tiêu"));
        add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // 3. VÙNG PHÍA NAM (SOUTH) - THANH CÔNG CỤ
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnAdd = new JButton("➕ Thêm Giao Dịch");
        JButton btnDelete = new JButton("❌ Xóa Giao Dịch");
        JButton btnRefresh = new JButton("🔄 Làm Mới Dữ Liệu");

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnRefresh);
        // ==========================================
        // 4. ĐỒNG BỘ SỰ KIỆN NÚT BẤM (ACTION LISTENERS)
        // ==========================================

        // Xử lý sự kiện cho nút "Làm Mới Dữ Liệu"
        btnRefresh.addActionListener(e -> {
            refreshTableData();
            JOptionPane.showMessageDialog(this, "Đã cập nhật dữ liệu mới nhất từ file!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        // Tự động nạp dữ liệu lên bảng ngay trong lần đầu tiên mở ứng dụng
        refreshTableData();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    /**
     * Đồng bộ dữ liệu từ ExpenseManager lên Bảng hiển thị (JTable) và Dashboard.
     */
    private void refreshTableData() {
        // 1. Xóa sạch dữ liệu cũ đang hiển thị trên bảng để chuẩn bị nạp mới
        tableModel.setRowCount(0);

        double totalIncome = 0;
        double totalExpense = 0;

        // 2. Duyệt qua toàn bộ giao dịch trong bộ nhớ và đưa vào TableModel
        for (com.moneybag.model.Transaction t : manager.getTransactions()) {
            String typeStr = t.getType().toString();
            double signedAmount = t.getSignedAmount();

            // Tính toán tổng dồn cho Dashboard
            if (signedAmount > 0) {
                totalIncome += signedAmount;
            } else {
                totalExpense += Math.abs(signedAmount);
            }

            // Tạo một hàng dữ liệu mới tương ứng với các cột của bảng
            Object[] rowData = {
                    t.getDate().toString(),
                    typeStr.equals("INCOME") ? "Thu nhập" : "Chi tiêu",
                    t.getCategory().getName(),
                    String.format("%,.0f VNĐ", t.getAmount()),
                    t.getWallet().getName(),
                    t.getNote()
            };

            // Thêm hàng vào bảng hiển thị
            tableModel.addRow(rowData);
        }

        // 3. Cập nhật con số hiển thị lên Dashboard phía trên
        lblTotalIncome.setText("Tổng Thu: " + String.format("%,.0f", totalIncome) + " VNĐ");
        lblTotalExpense.setText("Tổng Chi: " + String.format("%,.0f", totalExpense) + " VNĐ");

        double balance = totalIncome - totalExpense;
        lblBalance.setText("Số Dư: " + String.format("%,.0f", balance) + " VNĐ");

        // Đổi màu sắc số dư linh hoạt: Dương màu xanh, Âm màu đỏ
        if (balance >= 0) {
            lblBalance.setForeground(Color.BLUE);
        } else {
            lblBalance.setForeground(Color.RED);
        }
    }
}