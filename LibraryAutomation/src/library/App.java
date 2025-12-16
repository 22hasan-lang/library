package library;

import library.ui.MainFrame;

public class App {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.out.println("Tema yüklenemedi: " + e.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> new MainFrame());
    }
}
