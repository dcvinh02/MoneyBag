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
                    System.out.println("-> Chức năng Thêm giao dịch (Đang xây dựng...)");
                    break;
                case 2:
                    System.out.println("-> Chức năng Xóa giao dịch (Đang xây dựng...)");
                    break;
                case 3:
                    System.out.println("-> Chức năng Cập nhật giao dịch (Đang xây dựng...)");
                    break;
                case 4:
                    System.out.println("-> Chức năng Tìm kiếm giao dịch (Đang xây dựng...)");
                    break;
                case 5:
                    System.out.println("-> Chức năng Hiển thị tất cả giao dịch (Đang xây dựng...)");
                    break;
                case 6:
                    System.out.println("-> Chức năng Quản lý Danh mục (Đang xây dựng...)");
                    break;
                case 7:
                    System.out.println("-> Chức năng Quản lý Ví (Đang xây dựng...)");
                    break;
                case 8:
                    System.out.println("-> Chức năng Thống kê tháng (Đang xây dựng...)");
                    break;
                case 9:
                    System.out.println("-> Chức năng Cảnh báo ngân sách (Đang xây dựng...)");
                    break;
                case 0:
                    System.out.println("Cảm ơn bạn đã sử dụng My Expense Manager. Tạm biệt!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Action is not supported\n");
                    break;
            }
        }
    }

    /**
     * In ra Menu chính xác theo yêu cầu của đề bài.
     */
    private void printMenu() {
        System.out.println("\nWelcome to My Expense Manager!");
        System.out.println("[1] Add Transaction");
        System.out.println("[2] Remove Transaction");
        System.out.println("[3] Update Transaction");
        System.out.println("[4] Find Transaction");
        System.out.println("[5] Display All Transactions");
        System.out.println("[6] Manage Category");
        System.out.println("[7] Manage Wallet");
        System.out.println("[8] Monthly Summary");
        System.out.println("[9] Set/Check Budget");
        System.out.println("[0] Exit");
    }
}