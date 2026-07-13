package com.moneybag.service;

import com.moneybag.model.Budget;
import com.moneybag.model.Category;
import com.moneybag.model.Expense;
import com.moneybag.model.Transaction;
import com.moneybag.storage.Storage;
import com.moneybag.storage.CsvStorage;
import com.moneybag.wallet.Wallet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Lớp điều phối trung tâm quản lý toàn bộ logic nghiệp vụ các hệ thống
 * Áp dụng design pattern: singleton
 */
public class ExpenseManager {

    // tạo biến của singleton: tạo một biến static chứa thực thể duy nhất của lớp này
    private static ExpenseManager instance;

    // danh sách quản lý dữ liệu tron bộ nhớ (in-money data)
    private List<Transaction> transactions;
    private List<Wallet> wallets;
    private List<Category> categories;
    private Storage storage; // tham chiếu đến interface storage để đảm bảo tính đa hình
    private java.util.Map<Category, Budget> budgets;

    // để private constructor để không cho phép tạo đối tượng từ bên ngoài qua từ khóa 'new'
    private ExpenseManager() {
        this.transactions = new ArrayList<>();
        this.wallets = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.storage = new CsvStorage(); //
        this.budgets = new java.util.HashMap<>();
    }

    // cung cấp phương thức statics toàn cục để lấy ra thực thể duy nhất
    public static ExpenseManager getInstance() {
        if (instance == null) {
            instance = new ExpenseManager();
        }
        return instance;
    }

    // quản lý dữ liệu cơ bản
    public void addWallet(Wallet wallet) {
        if (wallet == null) return;
        this.wallets.add(wallet);
    }

    public void addCategory(Category category) {
        if (category == null) return;
        this.categories.add(category);
    }

    /**
     * Thêm giao dịch vào hệ thống
     * Khi thêm giao dịch, số dư của ví tương ứng sự tự động được cập nhật
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) return;
        // kiểm tra xem khoản chi mới có làm lố ngân sách giới hạn không
        if (transaction instanceof Expense) {
            checkBudgetWarning((Expense)  transaction);
        }

        Wallet wallet = transaction.getWallet();
        double amount = transaction.getAmount();

        // (đa hình) để thực hiện cập nhật số dư ví tương ứng
        if (transaction.getSignedAmount() > 0) {
            wallet.deposit(amount); // nếu là khỏoản thu thì nạp tiền vào ví
        } else {
            wallet.withdraw(amount);
        }

        this.transactions.add(transaction);
    }

    /**
     * Xóa 1 giao dịch trong 1 hệ thôn
     * hoàn taác (reverse) lại số tiền vào ví để đảm bảo chính xác số tiền mất đi
     */
    public boolean removeTransaction(String transactionId) {
        Transaction target = null;
        // tìm giao dịch cần xóa
        for (Transaction t: transactions) {
            if (t.getId().equals(transactionId)) {
                target = t;
                break;
            }
        }
        if (target == null) {
            System.out.println("Không tìm thấy giao dịch với ID: " + transactionId);
            return false;
        }
        // hoàn tác tiền vào ví
        Wallet wallet = target.getWallet();
        double amount = target.getAmount();
        if (target.getSignedAmount() > 0) {
            // xóa khoản thu -> trừ tiền khỏi ví
            wallet.withdraw(amount);
        } else  {
            wallet.deposit(amount);
        }
        transactions.remove(target);
        System.out.println("Đã xóa giao dịch thành công!");
        return true;

    }

    /**
     * Tìm kiếm gia dịch theo danh mục
     */
    public List<Transaction> findTransactions(Category category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t: transactions) {
            // so sánh tên danh mục
            if (t.getCategory().getName().equalsIgnoreCase(category.getName())) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Tìm kiếm danh mục theo ngày
     */
    public List<Transaction> findTransactions(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t: transactions) {
            java.time.LocalDate date = t.getDate();
            // kiểm tra ngày có nằm trong khoảng không ( bao gồm 2 đầu )
            if (!date.isBefore(startDate) && date.isAfter(endDate)) {
                result.add(t);
            }
        }
        return result;
    }
    /**
     * Khi sửa đổi số tiền giao dịch nếu sửa đổi truwcj tiếp sẽ rất dễ bug nên thay bằng xóa giao dịch cũ thêm giao dịch mới
     * Cập nhật 1 giao dịch đã tồn tại
     */
    public boolean updateTransaction(String transactionId, Transaction newTransaction) {
        // hàm removeTransaction đã bao gồm logic vaf hoàn lại tiền vào ví cũ
        boolean isRemoved = removeTransaction(transactionId);
        if (isRemoved) {
            // hàm addTransaction đã bao gồm cộng trừ vào ví mới
            addTransaction(newTransaction);
            System.out.println("Cập nhật giao dịch thành công");
            return true;
        }
        System.out.println("Cập nhật thất bại do không tìm thấy giao dịch cũ.");
        return false;
    }

    /**
     * Thống kê tổng thu, tổng chi và số dư trong 1 tháng cụ thể
     *
     */

    public void displayMonthlySummary(int month, int year) {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction t: transactions) {
            java.time.LocalDate date = t.getDate();
            //lọc các giao dịch trùng tháng và năm
            if (date.getMonthValue() == month && date.getYear() == year) {
                if (t.getSignedAmount() > 0) {
                    totalIncome += t.getSignedAmount();
                } else  {
                    totalExpense += Math.abs(t.getSignedAmount());
                }
            }
        }
        double balance = totalIncome - totalExpense;

        System.out.println("=== THỐNG KÊ THÁNG " + month + "/" + year + " ===");
        System.out.printf("Tổng Thu: +%,.0f VNĐ\n", totalIncome);
        System.out.printf("Tổng Chi: -%,.0f VNĐ\n", totalExpense);
        System.out.printf("Số dư tháng: %,.0f VNĐ\n", balance);
        System.out.println("=================================");
    }

    /**
     * Tính tổng số dư hiện tại của các vị trí cộng lai
     */
    public double getTotalBalance() {
        double total = 0;
        for (Wallet wallet : wallets) {
            total += wallet.getBalance();
        }
        return total;
    }
    // getter để view truy xuất dữ liệu
    public List<Transaction> getTransactions() { return transactions; }
    public List<Wallet> getWallets() { return wallets; }
    public List<Category> getCategories() { return categories; }

    /**
     * Đặt hạn mức ngân sách cho một danh mục
     */
    public void addBudget(Budget budget) {
        if (budget == null) return;
        // đưa vào map với key là category và value là budget
        this.budgets.put(budget.getCategory(), budget);
    }

    /**
     * kiểm tra in ra cảnh báo nếu khoán chi vượt ngân sách
     */
    public void checkBudgetWarning(Expense newExpense) {
        Category cat =  newExpense.getCategory();
        // nếu danh mục không được đặt ngân sách thì bỏ qua
        if (!budgets.containsKey(cat)) return;
        Budget budget = budgets.get(cat);

        double totalSpentInMonth = 0;

        int currentMonth = newExpense.getDate().getMonthValue();
        int currentYear = newExpense.getDate().getYear();

        // tính tổng các khoản chi cùng tháng, cùng năm và cùng danh mục
        for (Transaction t: transactions) {
            if (t instanceof Expense && t.getCategory().getName().equals(cat.getName())) {
                if (t.getDate().getMonthValue() == currentMonth && t.getDate().getYear() == currentYear) {
                    totalSpentInMonth += Math.abs(t.getSignedAmount());
                }
            }
        }
        // cộng thêm số tiền của khaorn chi đang chuẩn bị thêm vào
        totalSpentInMonth += newExpense.getAmount();

        // gọi hàm đối tượng budget để kểm tra
        if (budget.isExeeded(totalSpentInMonth)) {
            System.out.println("[Cảnh báo ngân sách]: Bạn đã chi tiêu " +
                    String.format("%,.0f", totalSpentInMonth) + " VNĐ cho danh mục [" +
                    cat.getName() + "]. " + " Vượt quá hạn mức " + String.format("%,.0f", budget.getLimit()) + " VNĐ!");
        }
    }
}

