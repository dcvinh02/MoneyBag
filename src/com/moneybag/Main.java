package com.moneybag;

import com.moneybag.gui.MainFrame;
import javax.swing.SwingUtilities;

/**import com.moneybag.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo tầng View và bắt đầu vòng lặp chương trình
        ConsoleView view = new ConsoleView();
        view.start();
    }
}*/


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}