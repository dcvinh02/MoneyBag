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
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnAddTx = new JButton("➕ Thêm Giao Dịch");
        JButton btnAddWallet = new JButton("💳 Thêm Ví");
        JButton btnAddCategory = new JButton("🏷️ Thêm Danh Mục");
        JButton btnUtils = new JButton("🛠️ Tiện Ích");
        JButton btnDelete = new JButton("❌ Xóa Giao Dịch");
        JButton btnRefresh = new JButton("🔄 Làm Mới");

        bottomPanel.add(btnAddTx);
        bottomPanel.add(btnAddWallet);
        bottomPanel.add(btnAddCategory);
        bottomPanel.add(btnUtils);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
        // ==========================================
        // 4. ĐỒNG BỘ SỰ KIỆN NÚT BẤM (ACTION LISTENERS)
        // ==========================================

        // Xử lý sự kiện cho nút "Thêm Giao Dịch" (Sửa lại biến tương ứng nếu cần)
        btnAddTx.addActionListener(e -> {
            showAddTransactionDialog();
        });

        // Xử lý sự kiện cho nút "Thêm Ví"
        btnAddWallet.addActionListener(e -> {
            showAddWalletDialog();
        });

        // Xử lý sự kiện cho nút "Thêm Danh Mục"
        btnAddCategory.addActionListener(e -> {
            showAddCategoryDialog();
        });

        // Xử lý sự kiện cho nút "Tiện Ích"
        btnUtils.addActionListener(e -> {
            showUtilityDialog();
        });
        // Tự động nạp dữ liệu lên bảng ngay trong lần đầu tiên mở ứng dụng
        refreshTableData();
        add(bottomPanel, BorderLayout.SOUTH);

        // Xử lý sự kiện cho nút "Xóa Giao Dịch"
        btnDelete.addActionListener(e -> {
            // Lấy ra vị trí (index) của dòng đang được click chọn trên bảng
            int selectedRow = transactionTable.getSelectedRow();

            // Nếu người dùng chưa chọn dòng nào (index = -1) thì cảnh báo
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn một giao dịch trên bảng để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Hiển thị hộp thoại xác nhận (Yes/No)
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn xóa giao dịch này không?\nLưu ý: Số tiền sẽ được hoàn lại vào ví tương ứng.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Vì thứ tự nạp dữ liệu trên bảng hiển thị khớp 100% với thứ tự trong List của Manager
                com.moneybag.model.Transaction txToRemove = manager.getTransactions().get(selectedRow);
                String idToRemove = txToRemove.getId();

                // Giao cho Manager xử lý logic xóa (hoàn tiền, trừ tiền)
                manager.removeTransaction(idToRemove);

                // Làm mới lại bảng và Dashboard để hiển thị con số mới nhất
                refreshTableData();
                JOptionPane.showMessageDialog(this, "✅ Xóa giao dịch thành công!");
            }
        });
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
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Object[] rowData = {
                    t.getDate().format(formatter),
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

    // popup nhập giao dịch mưới
    /**
     * Hiển thị cửa sổ Popup để người dùng nhập thông tin giao dịch mới.
     */
    /**
     * Hiển thị cửa sổ Popup để người dùng nhập thông tin giao dịch mới.
     */
    private void showAddTransactionDialog() {
        JDialog dialog = new JDialog(this, "Thêm Giao Dịch Mới", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 10, 15));

        // 1. Chuẩn bị danh sách Ví
        JComboBox<String> cbWallet = new JComboBox<>();
        for (com.moneybag.wallet.Wallet w : manager.getWallets()) {
            cbWallet.addItem(w.getName() + " (" + String.format("%,.0f", w.getBalance()) + " VNĐ)");
        }

        // 2. Chuẩn bị danh sách Danh mục (ẨN DANH MỤC HỆ THỐNG)
        JComboBox<String> cbCategory = new JComboBox<>();
        java.util.List<com.moneybag.model.Category> visibleCategories = new java.util.ArrayList<>();

        for (com.moneybag.model.Category c : manager.getCategories()) {
            // Chỉ thêm vào danh sách hiển thị nếu tên KHÔNG PHẢI là "Khởi tạo"
            if (!c.getName().equalsIgnoreCase("Khởi tạo")) {
                visibleCategories.add(c);
                //cbCategory.addItem(c.getName() + " [" + c.getType() + "]");
                String typeVietnamese = (c.getType() == com.moneybag.constant.TransactionType.INCOME) ? "Thu" : "Chi";
                cbCategory.addItem(c.getName() + " [" + typeVietnamese + "]");
            }
        }

        JTextField txtAmount = new JTextField();
        JTextField txtNote = new JTextField();
        JTextField txtExtra = new JTextField();

        dialog.add(new JLabel(" Chọn Ví:"));
        dialog.add(cbWallet);
        dialog.add(new JLabel(" Chọn Danh mục:"));
        dialog.add(cbCategory);
        dialog.add(new JLabel(" Số tiền (VNĐ):"));
        dialog.add(txtAmount);
        dialog.add(new JLabel(" Ghi chú:"));
        dialog.add(txtNote);
        dialog.add(new JLabel(" Nguồn / Phương thức TT:"));
        dialog.add(txtExtra);

        JButton btnSave = new JButton("Lưu Giao Dịch");
        //btnSave.setBackground(new Color(0, 153, 76)); // Nút màu xanh lá
        //btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            try {
                int walletIdx = cbWallet.getSelectedIndex();
                int catIdx = cbCategory.getSelectedIndex();

                if (walletIdx < 0 || catIdx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng tạo Ví và Danh mục trước!");
                    return;
                }

                // Lấy Ví từ Manager, nhưng lấy Danh mục từ mảng visibleCategories đã lọc
                com.moneybag.wallet.Wallet selectedWallet = manager.getWallets().get(walletIdx);
                com.moneybag.model.Category selectedCategory = visibleCategories.get(catIdx);

                double amount = Double.parseDouble(txtAmount.getText());
                String note = txtNote.getText();
                String extra = txtExtra.getText();

                com.moneybag.model.Transaction newTx = com.moneybag.factory.TransactionFactory.createTransaction(
                        selectedCategory.getType(), amount, java.time.LocalDateTime.now(), note, selectedCategory, selectedWallet, extra, null
                );

                manager.addTransaction(newTx);
                refreshTableData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "✅ Thêm giao dịch thành công!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Lỗi: Số tiền phải là chữ số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Lỗi nghiệp vụ: " + ex.getMessage(), "Từ chối giao dịch", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setVisible(true);
    }
    /**
     * Hiển thị Popup để tạo Ví mới ngay trên GUI.
     */
    private void showAddWalletDialog() {
        JDialog dialog = new JDialog(this, "Thêm Ví Mới", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(4, 2, 10, 15));

        JTextField txtName = new JTextField();
        JTextField txtBalance = new JTextField("0");
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ Ngân Hàng", "Ví điện tử"});

        dialog.add(new JLabel(" Tên ví/Tài khoản:"));
        dialog.add(txtName);
        dialog.add(new JLabel(" Số dư ban đầu (VNĐ):"));
        dialog.add(txtBalance);
        dialog.add(new JLabel(" Loại ví:"));
        dialog.add(cbType);

        JButton btnSave = new JButton("Lưu Ví");
        //btnSave.setBackground(new Color(0, 120, 215));
        //btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Tên ví không được để trống!");
                    return;
                }
                double balance = Double.parseDouble(txtBalance.getText().trim());
                int typeIdx = cbType.getSelectedIndex();

                com.moneybag.wallet.Wallet wallet;
                if (typeIdx == 1) {
                    wallet = new com.moneybag.wallet.BankAccount(name, 0, "Ngân hàng", "Chưa rõ");
                } else if (typeIdx == 2) {
                    wallet = new com.moneybag.wallet.EWallet(name, 0, "Ví điện tử");
                } else {
                    wallet = new com.moneybag.wallet.CashWallet(name, 0);
                }

                manager.addWallet(wallet);

                // Áp dụng chuẩn tư duy kế toán: Tạo giao dịch thu ban đầu để ghi nhận số tiền gốc
                if (balance > 0) {
                    com.moneybag.model.Category initCat = new com.moneybag.model.Category("Khởi tạo", com.moneybag.constant.TransactionType.INCOME);
                    if (manager.getCategories().stream().noneMatch(c -> c.getName().equals("Khởi tạo"))) {
                        manager.addCategory(initCat);
                    }
                    com.moneybag.model.Transaction openingTx = com.moneybag.factory.TransactionFactory.createTransaction(
                            com.moneybag.constant.TransactionType.INCOME, balance, java.time.LocalDateTime.now(), "Số dư ban đầu", initCat, wallet, "Hệ thống", null
                    );
                    manager.addTransaction(openingTx);
                }

                refreshTableData();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "✅ Đã thêm ví thành công!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "❌ Lỗi: Số dư phải là chữ số hợp lệ!");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setVisible(true);
    }

    /**
     * Hiển thị Popup để tạo Danh mục mới ngay trên GUI.
     */
    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog(this, "Thêm Danh Mục Mới", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(3, 2, 10, 15));

        JTextField txtName = new JTextField();
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Thu nhập", "Chi tiêu"});

        dialog.add(new JLabel(" Tên danh mục:"));
        dialog.add(txtName);
        dialog.add(new JLabel(" Loại danh mục:"));
        dialog.add(cbType);

        JButton btnSave = new JButton("Lưu Danh Mục");
        //btnSave.setBackground(new Color(230, 120, 0));
        //btnSave.setForeground(Color.WHITE);
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tên danh mục không được để trống!");
                return;
            }
            com.moneybag.constant.TransactionType type = cbType.getSelectedIndex() == 0
                    ? com.moneybag.constant.TransactionType.INCOME
                    : com.moneybag.constant.TransactionType.EXPENSE;

            com.moneybag.model.Category category = new com.moneybag.model.Category(name, type);
            manager.addCategory(category);

            dialog.dispose();
            JOptionPane.showMessageDialog(this, "✅ Đã thêm danh mục thành công!");
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnSave);
        dialog.add(btnCancel);
        dialog.setVisible(true);
    }
    /**
     * Hiển thị Hộp thoại Tiện ích tích hợp Tìm kiếm & Thiết lập Ngân sách.
     */
    private void showUtilityDialog() {
        JDialog dialog = new JDialog(this, "Trung Tâm Tiện Ích & Tra Cứu", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // JTabbedPane giúp phân chia giao diện thành nhiều Tab lựa chọn cực đẹp
        JTabbedPane tabbedPane = new JTabbedPane();

        // ==========================================
        // TAB 1: TÌM KIẾM THEO DANH MỤC
        // ==========================================
        JPanel tabCategory = new JPanel(new BorderLayout(10, 10));
        tabCategory.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel catSearchTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbSearchCat = new JComboBox<>();
        for (com.moneybag.model.Category c : manager.getCategories()) {
            cbSearchCat.addItem(c.getName());
        }
        JButton btnSearchCat = new JButton("Tìm");
        catSearchTop.add(new JLabel("Chọn Danh mục: "));
        catSearchTop.add(cbSearchCat);
        catSearchTop.add(btnSearchCat);
        tabCategory.add(catSearchTop, BorderLayout.NORTH);

        JTextArea txtCatResult = new JTextArea();
        txtCatResult.setEditable(false);
        tabCategory.add(new JScrollPane(txtCatResult), BorderLayout.CENTER);

        btnSearchCat.addActionListener(e -> {
            int idx = cbSearchCat.getSelectedIndex();
            if (idx >= 0) {
                com.moneybag.model.Category targetCat = manager.getCategories().get(idx);
                java.util.List<com.moneybag.model.Transaction> results = manager.findTransactions(targetCat);
                if (results.isEmpty()) {
                    txtCatResult.setText("Không tìm thấy giao dịch nào thuộc danh mục này.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (com.moneybag.model.Transaction t : results) {
                        sb.append(String.format("[%s] %s | %s | %,.0f VNĐ - %s\n",
                                t.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                t.getType() == com.moneybag.constant.TransactionType.INCOME ? "THU" : "CHI",
                                t.getCategory().getName(), t.getAmount(), t.getNote()));
                    }
                    txtCatResult.setText(sb.toString());
                }
            }
        });

        // ==========================================
        // TAB 2: TÌM KIẾM THEO KHOẢNG NGÀY (Nâng cấp UX)
        // ==========================================
        JPanel tabDate = new JPanel(new BorderLayout(10, 10));
        tabDate.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel dateSearchTop = new JPanel(new GridLayout(2, 2, 5, 5));

        // Tạo định dạng ngày quen thuộc với người Việt Nam
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.LocalDate today = java.time.LocalDate.now();

        // Tự động điền mặc định từ mùng 1 đến ngày hiện tại của tháng
        JTextField txtStartDate = new JTextField(today.withDayOfMonth(1).format(dateFormatter));
        JTextField txtEndDate = new JTextField(today.format(dateFormatter));
        JButton btnSearchDate = new JButton("Tìm kiếm");

        dateSearchTop.add(new JLabel("Từ ngày (dd/MM/yyyy):"));
        dateSearchTop.add(txtStartDate);
        dateSearchTop.add(new JLabel("Đến ngày (dd/MM/yyyy):"));
        dateSearchTop.add(txtEndDate);

        tabDate.add(dateSearchTop, BorderLayout.NORTH);

        JTextArea txtDateResult = new JTextArea();
        txtDateResult.setEditable(false);
        tabDate.add(new JScrollPane(txtDateResult), BorderLayout.CENTER);
        tabDate.add(btnSearchDate, BorderLayout.SOUTH);

        btnSearchDate.addActionListener(e -> {
            try {
                // 1. Chỉ parse ngày đơn giản từ giao diện người dùng
                java.time.LocalDate startDate = java.time.LocalDate.parse(txtStartDate.getText().trim(), dateFormatter);
                java.time.LocalDate endDate = java.time.LocalDate.parse(txtEndDate.getText().trim(), dateFormatter);

                // 2. Hệ thống tự động bọc thêm Giờ/Phút để đồng bộ với cơ sở dữ liệu LocalDateTime
                java.time.LocalDateTime startDateTime = startDate.atStartOfDay(); // Tự thêm 00:00:00
                java.time.LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // Tự thêm 23:59:59

                java.util.List<com.moneybag.model.Transaction> results = manager.findTransactions(startDateTime, endDateTime);

                if (results.isEmpty()) {
                    txtDateResult.setText("Không có giao dịch nào trong khoảng thời gian này.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (com.moneybag.model.Transaction t : results) {
                        sb.append(String.format("[%s] %s | %,.0f VNĐ - %s\n",
                                t.getDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                t.getCategory().getName(), t.getAmount(), t.getNote()));
                    }
                    txtDateResult.setText(sb.toString());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Định dạng ngày không hợp lệ!\nVui lòng nhập theo chuẩn dd/MM/yyyy (Ví dụ: 14/07/2026)");
            }
        });

        // ==========================================
        // TAB 3: THIẾT LẬP NGÂN SÁCH
        // ==========================================
        JPanel tabBudget = new JPanel(new GridLayout(4, 2, 10, 20));
        tabBudget.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> cbBudgetCat = new JComboBox<>();
        // Chỉ hiện danh mục CHI TIÊU để đặt hạn mức chi tiêu
        java.util.List<com.moneybag.model.Category> expenseCats = new java.util.ArrayList<>();
        for (com.moneybag.model.Category c : manager.getCategories()) {
            if (c.getType() == com.moneybag.constant.TransactionType.EXPENSE) {
                expenseCats.add(c);
                cbBudgetCat.addItem(c.getName());
            }
        }

        JTextField txtBudgetLimit = new JTextField();
        JButton btnSaveBudget = new JButton("Thiết lập ngân sách");

        tabBudget.add(new JLabel("Chọn Danh mục chi tiêu:"));
        tabBudget.add(cbBudgetCat);
        tabBudget.add(new JLabel("Hạn mức tối đa/tháng (VNĐ):"));
        tabBudget.add(txtBudgetLimit);
        tabBudget.add(new JLabel("")); // ô trống giữ khoảng cách
        tabBudget.add(btnSaveBudget);

        btnSaveBudget.addActionListener(e -> {
            try {
                int idx = cbBudgetCat.getSelectedIndex();
                if (idx < 0) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng tạo Danh mục chi tiêu trước!");
                    return;
                }
                com.moneybag.model.Category targetCat = expenseCats.get(idx);
                double limit = Double.parseDouble(txtBudgetLimit.getText().trim());

                // Khởi tạo đối tượng Budget và đưa vào hệ thống quản lý
                com.moneybag.model.Budget budget = new com.moneybag.model.Budget(targetCat, limit, com.moneybag.constant.Period.MONTHLY);
                manager.addBudget(budget);

                JOptionPane.showMessageDialog(dialog, "✅ Đã thiết lập hạn mức " + String.format("%,.0f", limit) + " VNĐ cho danh mục [" + targetCat.getName() + "] thành công!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: Hạn mức nhập vào phải là số!");
            }
        });

        // Thêm các tab vào panel chính
        tabbedPane.addTab("🔍 Tìm Theo Danh Mục", tabCategory);
        tabbedPane.addTab("📅 Tìm Theo Ngày", tabDate);
        tabbedPane.addTab("💰 Đặt Ngân Sách", tabBudget);

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }
}