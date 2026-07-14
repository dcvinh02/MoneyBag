package com.moneybag.view;

import com.moneybag.service.ExpenseManager;

import java.util.Scanner;

/**
 * Lớp chịu trách nhiệm hiển thị giao diện dòng lệnh (CLI) và tương tác với người dùng.
 * Tầng View KHÔNG chứa logic tính toán, chỉ làm nhiệm vụ Nhập/Xuất và gọi xuống tầng Service.
 */
public class ConsoleView {
    private Scanner scanner;
    private ExpenseManager manager;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        // Gọi Singleton để lấy ra bản thể duy nhất của hệ thống quản lý
        this.manager = ExpenseManager.getInstance();
    }

    /**
     * Vòng lặp chính của chương trình.
     */
    public void start() {
        // nạp dữ liệu
        System.out.println("Đang khởi động hệ thống...");
        manager.loadData();

        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            System.out.print("Nhập lựa chọn của bạn: ");
            String input = scanner.nextLine();

            // Kiểm tra xem input có phải là số từ 0 đến 9 không bằng Regular Expression
            if (!input.matches("[0-9]")) {
                System.out.println("Action is not supported\n"); // Yêu cầu bắt buộc của đề bài
                continue; // Bỏ qua các lệnh dưới, quay lại vòng lặp mới
            }

            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    removeTransaction();
                    break;
                case 3:
                    updateTransaction();
                    break;
                case 4:
                    findTransaction();
                    break;
                case 5:
                    displayAllTransactions();
                    break;
                case 6:
                    manageCategory();
                    break;
                case 7:
                    manageWallet();
                    break;
                case 8:
                    monthlySummary();
                    break;
                case 9:
                    manageBudget();
                    break;
                case 0:
                    manager.saveData();
                    System.out.println("Cảm ơn bạn đã sử dụng MoneyBag. Tạm biệt!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Hành động không được hỗ trợ\n");
                    break;
            }
        }
    }

    /**
     * In ra Menu chính xác theo yêu cầu của đề bài.
     */
    private void printMenu() {
        System.out.println("\nChào mừng tới MoneyBag!");
        System.out.println("[1] Thêm giao dịch");
        System.out.println("[2] Xóa giao dịch");
        System.out.println("[3] Cập nhật giao dịch");
        System.out.println("[4] Tìm giao dịch");
        System.out.println("[5] Hiển thị tất cả giao dịch");
        System.out.println("[6] Quản lý danh mục");
        System.out.println("[7] Quản lý ví");
        System.out.println("[8] Tóm tắt hàng tháng");
        System.out.println("[9] Cài đặt/kiểm tra ngân sách");
        System.out.println("[0] Thoát");
    }
    /**
     * Chức năng [6]: Thêm danh mục thu/chi mới.
     */
    private void manageCategory() {
        System.out.println("\n--- THÊM DANH MỤC MỚI ---");
        System.out.print("Nhập tên danh mục (VD: Lương, Ăn uống, Mua sắm...): ");
        String name = scanner.nextLine();

        System.out.println("Loại danh mục: [1] Thu nhập (INCOME)   [2] Chi tiêu (EXPENSE)");
        System.out.print("Lựa chọn (1 hoặc 2): ");
        String typeInput = scanner.nextLine();

        // Sử dụng toán tử 3 ngôi (Ternary operator) cho gọn code
        com.moneybag.constant.TransactionType type = typeInput.equals("1")
                ? com.moneybag.constant.TransactionType.INCOME
                : com.moneybag.constant.TransactionType.EXPENSE;

        com.moneybag.model.Category category = new com.moneybag.model.Category(name, type);
        manager.addCategory(category);
        System.out.println("✅ Đã thêm danh mục thành công: " + name + " (" + type + ")");
    }

    /**
     * Chức năng [7]: Thêm ví/tài khoản mới.
     * Áp dụng tính Đa hình khi khởi tạo các loại ví khác nhau.
     */
    private void manageWallet() {
        System.out.println("\n--- THÊM VÍ / TÀI KHOẢN MỚI ---");
        System.out.print("Nhập tên ví (VD: Ví tiền mặt, Vietcombank, MoMo): ");
        String name = scanner.nextLine();

        System.out.print("Nhập số dư ban đầu (VNĐ): ");
        double initialBalance = 0;
        try {
            initialBalance = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Bạn nhập không phải là số! Số dư mặc định sẽ là 0 VNĐ.");
        }

        System.out.println("Chọn loại ví: [1] Tiền mặt   [2] Thẻ Ngân Hàng   [3] Ví điện tử");
        System.out.print("Lựa chọn (1-3): ");
        String typeInput = scanner.nextLine();

        com.moneybag.wallet.Wallet wallet;

        // BƯỚC NGOẶT: Khởi tạo ví với số dư = 0 để quản lý dòng tiền 100% qua Giao dịch
        switch (typeInput) {
            case "2":
                System.out.print("Nhập tên Ngân hàng (VD: TPBank): ");
                String bankName = scanner.nextLine();
                System.out.print("Nhập số tài khoản: ");
                String accNum = scanner.nextLine();
                wallet = new com.moneybag.wallet.BankAccount(name, 0, bankName, accNum);
                break;
            case "3":
                System.out.print("Nhập tên nhà cung cấp (VD: MoMo, ZaloPay): ");
                String provider = scanner.nextLine();
                wallet = new com.moneybag.wallet.EWallet(name, 0, provider);
                break;
            case "1":
            default:
                wallet = new com.moneybag.wallet.CashWallet(name, 0);
                break;
        }

        manager.addWallet(wallet);

        // NẾU CÓ SỐ DƯ, HỆ THỐNG SẼ TỰ ĐỘNG TẠO 1 GIAO DỊCH THU ĐỂ GHI SỔ (Auto-save an toàn)
        if (initialBalance > 0) {
            com.moneybag.model.Category initCat = new com.moneybag.model.Category("Khởi tạo", com.moneybag.constant.TransactionType.INCOME);
            // Chỉ thêm danh mục nếu chưa có
            if (manager.getCategories().stream().noneMatch(c -> c.getName().equals("Khởi tạo"))) {
                manager.addCategory(initCat);
            }

            com.moneybag.model.Transaction openingTx = com.moneybag.factory.TransactionFactory.createTransaction(
                    com.moneybag.constant.TransactionType.INCOME,
                    initialBalance,
                    java.time.LocalDate.now(),
                    "Số dư ban đầu",
                    initCat,
                    wallet,
                    "Hệ thống",
                    null
            );
            manager.addTransaction(openingTx);
        }

        System.out.println("✅ Đã thêm ví thành công: " + name + " | Số dư: " + String.format("%,.0f", wallet.getBalance()) + " VNĐ");
    }
    /**
     * Chức năng [1]: Thêm giao dịch mới.
     */
    private void addTransaction() {
        System.out.println("\n--- THÊM GIAO DỊCH MỚI ---");

        // 1. Kiểm tra điều kiện: Phải có ít nhất 1 Ví và 1 Danh mục mới cho phép thêm
        if (manager.getWallets().isEmpty()) {
            System.out.println("❌ Bạn chưa có ví nào. Vui lòng chọn [7] để thêm ví trước!");
            return;
        }
        if (manager.getCategories().isEmpty()) {
            System.out.println("❌ Bạn chưa có danh mục nào. Vui lòng chọn [6] để thêm danh mục trước!");
            return;
        }

        try {
            // 2. Chọn Ví
            System.out.println("Danh sách Ví:");
            for (int i = 0; i < manager.getWallets().size(); i++) {
                System.out.println((i + 1) + ". " + manager.getWallets().get(i).getName()
                        + " (Số dư: " + String.format("%,.0f", manager.getWallets().get(i).getBalance()) + " VNĐ)");
            }
            System.out.print("Chọn Ví (nhập số thứ tự): ");
            int walletIndex = Integer.parseInt(scanner.nextLine()) - 1;
            com.moneybag.wallet.Wallet selectedWallet = manager.getWallets().get(walletIndex);

            // 3. Chọn Danh mục
            System.out.println("Danh sách Danh mục:");
            for (int i = 0; i < manager.getCategories().size(); i++) {
                System.out.println((i + 1) + ". " + manager.getCategories().get(i).getName()
                        + " [" + manager.getCategories().get(i).getType() + "]");
            }
            System.out.print("Chọn Danh mục (nhập số thứ tự): ");
            int categoryIndex = Integer.parseInt(scanner.nextLine()) - 1;
            com.moneybag.model.Category selectedCategory = manager.getCategories().get(categoryIndex);

            // 4. Nhập số tiền và Ghi chú
            System.out.print("Nhập số tiền (VNĐ): ");
            double amount = Double.parseDouble(scanner.nextLine());

            System.out.print("Nhập ghi chú ngắn gọn: ");
            String note = scanner.nextLine();

            // 5. Thu thập thông tin phụ tùy vào loại danh mục
            String extraInfo = "";
            com.moneybag.constant.TransactionType type = selectedCategory.getType();
            if (type == com.moneybag.constant.TransactionType.INCOME) {
                System.out.print("Nhập Nguồn thu (VD: Lương công ty, Bán hàng): ");
                extraInfo = scanner.nextLine();
            } else {
                System.out.print("Nhập Phương thức thanh toán (VD: Quẹt thẻ, Tiền mặt): ");
                extraInfo = scanner.nextLine();
            }

            // 6. Đưa nguyên liệu vào Nhà máy (Factory) để sản xuất Giao dịch.
            // Tạm thời gán ngày là hôm nay (LocalDate.now())
            com.moneybag.model.Transaction newTx = com.moneybag.factory.TransactionFactory.createTransaction(
                    type, amount, java.time.LocalDate.now(), note, selectedCategory, selectedWallet, extraInfo, null
            );

            // 7. Giao cho Manager xử lý (Manager sẽ tự lo việc cộng/trừ tiền trong ví)
            manager.addTransaction(newTx);
            System.out.println("✅ Đã thêm giao dịch thành công!");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("❌ Lỗi: Bạn chọn số thứ tự không có trong danh sách!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Bạn phải nhập chữ số hợp lệ cho số tiền hoặc số thứ tự!");
        } catch (Exception e) {
            // Catch cản đáy: Bắt mọi lỗi bất ngờ khác như rút tiền không đủ số dư
            System.out.println("❌ Lỗi: " + e.getMessage());
        }
    }

    /**
     * Chức năng [5]: Hiển thị tất cả giao dịch.
     * Tận dụng Đa hình: Lệnh t.printInfo() sẽ tự biết in kiểu Thu hay kiểu Chi.
     */
    private void displayAllTransactions() {
        System.out.println("\n--- LỊCH SỬ GIAO DỊCH ---");
        java.util.List<com.moneybag.model.Transaction> list = manager.getTransactions();

        if (list.isEmpty()) {
            System.out.println("Trống. Chưa có giao dịch nào được ghi nhận.");
            return;
        }

        for (com.moneybag.model.Transaction t : list) {
            t.printInfo();
        }
    }
    /**
     * Chức năng [2]: Xóa giao dịch.
     * Mẹo UX: Vì ID của giao dịch là chuỗi UUID rất dài, ta không bắt người dùng gõ UUID.
     * Ta in ra danh sách theo số thứ tự (1, 2, 3...) và cho người dùng chọn số.
     */
    private void removeTransaction() {
        System.out.println("\n--- XÓA GIAO DỊCH ---");
        java.util.List<com.moneybag.model.Transaction> list = manager.getTransactions();

        if (list.isEmpty()) {
            System.out.println("❌ Không có giao dịch nào để xóa.");
            return;
        }

        // In danh sách kèm số thứ tự
        for (int i = 0; i < list.size(); i++) {
            System.out.print((i + 1) + ". ");
            list.get(i).printInfo();
        }

        try {
            System.out.print("Chọn giao dịch muốn xóa (nhập số thứ tự): ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;

            // Lấy ra ID thực sự của giao dịch từ vị trí index
            String idToRemove = list.get(index).getId();
            manager.removeTransaction(idToRemove);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("❌ Lỗi: Số thứ tự không hợp lệ!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Vui lòng nhập một số!");
        }
    }

    /**
     * Chức năng [8]: Thống kê hàng tháng.
     */
    private void monthlySummary() {
        System.out.println("\n--- THỐNG KÊ HÀNG THÁNG ---");
        try {
            System.out.print("Nhập tháng (1-12): ");
            int month = Integer.parseInt(scanner.nextLine());
            System.out.print("Nhập năm (VD: 2026): ");
            int year = Integer.parseInt(scanner.nextLine());

            manager.displayMonthlySummary(month, year);
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Vui lòng nhập số hợp lệ!");
        }
    }

    /**
     * Chức năng [9]: Thiết lập cảnh báo ngân sách.
     */
    private void manageBudget() {
        System.out.println("\n--- THIẾT LẬP NGÂN SÁCH ---");
        if (manager.getCategories().isEmpty()) {
            System.out.println("❌ Bạn chưa có danh mục nào. Vui lòng thêm danh mục trước!");
            return;
        }

        System.out.println("Danh sách Danh mục:");
        for (int i = 0; i < manager.getCategories().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getCategories().get(i).getName());
        }

        try {
            System.out.print("Chọn Danh mục để đặt ngân sách (nhập số thứ tự): ");
            int catIndex = Integer.parseInt(scanner.nextLine()) - 1;
            com.moneybag.model.Category selectedCategory = manager.getCategories().get(catIndex);

            System.out.print("Nhập hạn mức tối đa mỗi tháng (VNĐ): ");
            double limit = Double.parseDouble(scanner.nextLine());

            com.moneybag.model.Budget budget = new com.moneybag.model.Budget(
                    selectedCategory, limit, com.moneybag.constant.Period.MONTHLY
            );

            manager.addBudget(budget);
            System.out.println("✅ Đã thiết lập ngân sách thành công cho danh mục ["
                    + selectedCategory.getName() + "]: " + String.format("%,.0f", limit) + " VNĐ");
        } catch (Exception e) {
            System.out.println("❌ Lỗi: Đầu vào không hợp lệ!");
        }
    }
    /**
     * Chức năng [4]: Tìm kiếm giao dịch.
     * Cho phép tìm theo Danh mục (Category) đã thiết lập trong ExpenseManager.
     */
    private void findTransaction() {
        System.out.println("\n--- TÌM KIẾM GIAO DỊCH ---");
        if (manager.getCategories().isEmpty()) {
            System.out.println("❌ Chưa có danh mục nào để tìm kiếm.");
            return;
        }

        System.out.println("Chọn Danh mục muốn tìm:");
        for (int i = 0; i < manager.getCategories().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getCategories().get(i).getName());
        }

        try {
            System.out.print("Nhập số thứ tự danh mục: ");
            int catIndex = Integer.parseInt(scanner.nextLine()) - 1;
            com.moneybag.model.Category searchCategory = manager.getCategories().get(catIndex);

            // Gọi hàm tìm kiếm từ Manager
            java.util.List<com.moneybag.model.Transaction> results = manager.findTransactions(searchCategory);

            if (results.isEmpty()) {
                System.out.println("Không tìm thấy giao dịch nào thuộc danh mục [" + searchCategory.getName() + "].");
            } else {
                System.out.println("🔍 KẾT QUẢ TÌM KIẾM:");
                for (com.moneybag.model.Transaction t : results) {
                    t.printInfo();
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi: Đầu vào không hợp lệ!");
        }
    }

    /**
     * Chức năng [3]: Cập nhật (Sửa) giao dịch.
     * Logic: Chọn giao dịch cũ -> Nhập thông tin mới -> Manager sẽ tự xóa cũ, thêm mới.
     */
    private void updateTransaction() {
        System.out.println("\n--- CẬP NHẬT GIAO DỊCH ---");
        java.util.List<com.moneybag.model.Transaction> list = manager.getTransactions();

        if (list.isEmpty()) {
            System.out.println("❌ Không có giao dịch nào để cập nhật.");
            return;
        }

        // 1. Chọn giao dịch cần sửa
        for (int i = 0; i < list.size(); i++) {
            System.out.print((i + 1) + ". ");
            list.get(i).printInfo();
        }

        try {
            System.out.print("Chọn giao dịch muốn SỬA (nhập số thứ tự): ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            com.moneybag.model.Transaction oldTx = list.get(index);
            String oldId = oldTx.getId();

            System.out.println("Giao dịch đang sửa: " + oldTx.getNote() + " (" + oldTx.getAmount() + " VNĐ)");
            System.out.println("⚠️ Lưu ý: Để đơn giản hóa, vui lòng nhập lại số tiền và ghi chú mới (Giữ nguyên Ví và Danh mục cũ).");

            // 2. Nhập thông tin mới
            System.out.print("Nhập số tiền MỚI (VNĐ): ");
            double newAmount = Double.parseDouble(scanner.nextLine());

            System.out.print("Nhập ghi chú MỚI: ");
            String newNote = scanner.nextLine();

            // 3. Tạo đối tượng Transaction mới bằng Factory (Kế thừa Ví, Danh mục, Nguồn/Phương thức cũ)
            String extraInfo = "";
            if (oldTx instanceof com.moneybag.model.Income) {
                extraInfo = ((com.moneybag.model.Income) oldTx).getSource();
            } else if (oldTx instanceof com.moneybag.model.Expense) {
                extraInfo = ((com.moneybag.model.Expense) oldTx).getPaymentMethod();
            }

            com.moneybag.model.Transaction newTx = com.moneybag.factory.TransactionFactory.createTransaction(
                    oldTx.getType(),
                    newAmount,
                    oldTx.getDate(),
                    newNote,
                    oldTx.getCategory(),
                    oldTx.getWallet(),
                    extraInfo,
                    null
            );

            // 4. Giao cho Manager xử lý đổi cũ lấy mới
            manager.updateTransaction(oldId, newTx);

        } catch (Exception e) {
            System.out.println("❌ Lỗi: Cập nhật thất bại do đầu vào không hợp lệ!");
        }
    }
}