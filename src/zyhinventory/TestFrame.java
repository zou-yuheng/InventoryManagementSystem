package zyhinventory;

import javax.swing.*;

public class TestFrame {
    public static void main(String[] args) {
        System.out.println("Test starting...");
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            System.out.println("Frame should be visible now");
        });
    }
}