package library.ui;

import library.service.AuthServiceImpl; // Servisimizi import ettik

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    // Veritabanı Servisi
    private AuthServiceImpl authService = new AuthServiceImpl();

    // Renkler
    private final Color BG_COLOR = new Color(30, 31, 38);       // Arka plan
    private final Color CARD_COLOR = new Color(44, 47, 51);     // Kart rengi
    private final Color ACCENT_COLOR = new Color(52, 152, 219); // Mavi buton

    public MainFrame() {
        setTitle("Kütüphane Sistemi Giriş");
        setSize(450, 550); // Daha kompakt bir pencere
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Ekranın ortasında başla
        setResizable(false);

        // Ana Panel (Arka Plan)
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- GİRİŞ KARTI (Ortadaki Kutu) ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(null);
        cardPanel.setPreferredSize(new Dimension(350, 420));
        cardPanel.setBackground(CARD_COLOR);
        // Gölge efekti yerine sade border
        cardPanel.setBorder(new LineBorder(new Color(60, 63, 65), 1));

        // Logo / İkon
        JLabel lblIcon = new JLabel("📚");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setBounds(145, 30, 80, 70);
        cardPanel.add(lblIcon);

        // Başlık
        JLabel lblTitle = new JLabel("HOŞ GELDİNİZ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 100, 350, 30);
        cardPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Lütfen bilgilerinizi giriniz");
        lblSub.setForeground(Color.GRAY);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        lblSub.setBounds(0, 130, 350, 20);
        cardPanel.add(lblSub);

        // --- INPUTLAR ---

        // Kullanıcı Adı
        JLabel lblUser = new JLabel("Kullanıcı Adı");
        lblUser.setForeground(new Color(200, 200, 200));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setBounds(40, 170, 200, 20);
        cardPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 195, 270, 40);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 10, 5, 5)));
        cardPanel.add(txtUsername);

        // Şifre
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setForeground(new Color(200, 200, 200));
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setBounds(40, 245, 200, 20);
        cardPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 270, 270, 40);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 10, 5, 5)));
        cardPanel.add(txtPassword);

        // Enter tuşu ile giriş yapma özelliği
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKeyAdapter);
        txtPassword.addKeyListener(enterKeyAdapter);

        // --- BUTON ---
        JButton btnLogin = new JButton("GİRİŞ YAP");
        btnLogin.setBounds(40, 340, 270, 45);
        btnLogin.setBackground(ACCENT_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Efekti
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnLogin.setBackground(ACCENT_COLOR.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnLogin.setBackground(ACCENT_COLOR); }
        });

        btnLogin.addActionListener(e -> doLogin());
        cardPanel.add(btnLogin);

        mainPanel.add(cardPanel);
        setVisible(true);
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ESKİ: if (user.equals("admin") && pass.equals("123"))
        // YENİ: Veritabanından kontrol ediyoruz (admin / 1234)
        if (authService.login(user, pass)) {
            dispose(); // Login ekranını kapat
            new MenuPanel().setVisible(true); // Menüyü aç
        } else {
            JOptionPane.showMessageDialog(this, "Hatalı kullanıcı adı veya şifre!", "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
        }
    }
}