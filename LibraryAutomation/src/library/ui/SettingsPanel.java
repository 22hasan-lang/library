package library.ui;

import library.service.AuthServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JFrame {

    private AuthServiceImpl authService = new AuthServiceImpl();
    private final Color BG_COLOR = new Color(30, 31, 38);

    public SettingsPanel() {
        setTitle("Ayarlar");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        JLabel titleLabel = new JLabel("⚙️ Sistem Ayarları");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- TABS (Sekmeler) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(new Color(44, 47, 51));
        tabbedPane.setForeground(Color.WHITE);

        // 1. Sekme: Şifre Değiştir
        tabbedPane.addTab("Şifre Değiştir", createChangePasswordPanel());

        // 2. Sekme: Yeni Yönetici Ekle
        tabbedPane.addTab("Yönetici Ekle", createAddAdminPanel());

        // 3. Sekme: Hakkında
        tabbedPane.addTab("Hakkında", createAboutPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // --- FOOTER (Geri Butonu) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(new EmptyBorder(20, 40, 30, 40));

        JButton backBtn = new JButton("← Ana Menüye Dön");
        backBtn.setPreferredSize(new Dimension(200, 50));
        backBtn.setBackground(new Color(149, 165, 166));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            dispose();
            new MenuPanel().setVisible(true);
        });

        footerPanel.add(backBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // --- PANEL 1: Şifre Değiştirme ---
    private JPanel createChangePasswordPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_COLOR);

        JLabel lblUser = createLabel("Kullanıcı Adı:", 100, 50, panel);
        JTextField txtUser = createField(100, 90, panel);
        // Varsayılan olarak admin yazsın, kullanıcı değiştirebilir
        txtUser.setText("admin");

        JLabel lblOld = createLabel("Eski Şifre:", 100, 150, panel);
        JPasswordField txtOld = createPassField(100, 190, panel);

        JLabel lblNew = createLabel("Yeni Şifre:", 100, 250, panel);
        JPasswordField txtNew = createPassField(100, 290, panel);

        JButton btnSave = createButton("Şifreyi Güncelle", new Color(52, 152, 219), 100, 360);
        btnSave.addActionListener(e -> {
            String u = txtUser.getText();
            String o = new String(txtOld.getPassword());
            String n = new String(txtNew.getPassword());

            if (authService.changePassword(u, o, n)) {
                JOptionPane.showMessageDialog(this, "Şifre başarıyla değiştirildi!");
                txtOld.setText("");
                txtNew.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Hata! Eski şifre yanlış olabilir.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnSave);

        return panel;
    }

    // --- PANEL 2: Yeni Yönetici Ekleme ---
    private JPanel createAddAdminPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(BG_COLOR);

        JLabel lblInfo = new JLabel("<html>* Buradan sisteme yeni yönetici ekleyebilirsiniz.<br>Yeni yönetici tüm yetkilere sahip olacaktır.</html>");
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBounds(100, 20, 400, 40);
        panel.add(lblInfo);

        JLabel lblUser = createLabel("Yeni Kullanıcı Adı:", 100, 80, panel);
        JTextField txtUser = createField(100, 120, panel);

        JLabel lblPass = createLabel("Belirlenen Şifre:", 100, 180, panel);
        JPasswordField txtPass = createPassField(100, 220, panel);

        JButton btnAdd = createButton("Yöneticiyi Kaydet", new Color(46, 204, 113), 100, 290);
        btnAdd.addActionListener(e -> {
            String u = txtUser.getText();
            String p = new String(txtPass.getPassword());

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            if (authService.addAdmin(u, p)) {
                JOptionPane.showMessageDialog(this, "Yeni yönetici eklendi: " + u);
                txtUser.setText("");
                txtPass.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnAdd);

        return panel;
    }

    // --- PANEL 3: Hakkında ---
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        JLabel lbl = new JLabel("<html><center><h1>📚 Kütüphane Otomasyon Sistemi</h1>" +
                "<h3>Versiyon 1.0</h3>" +
                "<p>Geliştirici: [Senin Adın]</p>" +
                "<p>Teknolojiler: Java Swing, JDBC, MySQL</p>" +
                "<p>Copyright © 2025</p></center></html>");
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        return panel;
    }

    // --- YARDIMCI METODLAR ---
    private JLabel createLabel(String text, int x, int y, JPanel p) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setBounds(x, y, 200, 30);
        p.add(l);
        return l;
    }

    private JTextField createField(int x, int y, JPanel p) {
        JTextField t = new JTextField();
        t.setBounds(x, y, 300, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }

    private JPasswordField createPassField(int x, int y, JPanel p) {
        JPasswordField t = new JPasswordField();
        t.setBounds(x, y, 300, 40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(t);
        return t;
    }

    private JButton createButton(String text, Color bg, int x, int y) {
        JButton b = new JButton(text);
        b.setBounds(x, y, 200, 45);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        return b;
    }
}