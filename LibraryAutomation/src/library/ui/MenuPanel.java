package library.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JFrame {

    private final Color BG_COLOR = new Color(30, 31, 38);

    public MenuPanel() {
        setTitle("Kütüphane Yönetim Sistemi - Ana Menü");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Tam ekran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- HEADER (Üst Başlık) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        JLabel titleLabel = new JLabel("Kütüphane Yönetim Paneli");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- BUTTON GRID (Ortadaki Butonlar) ---
        // Ekranın ortasına hizalamak için GridBagLayout
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_COLOR);

        // GÜNCELLEME: 5 buton olduğu için sütun sayısını 3 yaptık (2 satır, 3 sütun)
        JPanel gridPanel = new JPanel(new GridLayout(2, 3, 30, 30));
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setPreferredSize(new Dimension(1000, 500)); // Genişliği biraz artırdık

        // 1. Kitap İşlemleri
        gridPanel.add(createMenuButton("Kitap İşlemleri", "📚", new Color(52, 152, 219), e -> {
            dispose(); new BookPanel().setVisible(true);
        }));

        // 2. Üye İşlemleri
        gridPanel.add(createMenuButton("Üye İşlemleri", "👥", new Color(46, 204, 113), e -> {
            dispose(); new MemberPanel().setVisible(true);
        }));

        // 3. Ödünç & İade
        gridPanel.add(createMenuButton("Ödünç & İade", "📝", new Color(241, 196, 15), e -> {
            dispose(); new LoanPanel().setVisible(true);
        }));

        // 4. İstatistikler
        gridPanel.add(createMenuButton("İstatistikler", "📊", new Color(155, 89, 182), e -> {
            dispose(); new StatisticsPanel().setVisible(true);
        }));

        // 5. Ayarlar (YENİ EKLENDİ)
        gridPanel.add(createMenuButton("Ayarlar", "⚙️", new Color(52, 73, 94), e -> {
            dispose(); new SettingsPanel().setVisible(true);
        }));

        // 6. Boşluk (Grid düzgün dursun diye boş panel - İsteğe bağlı)
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(BG_COLOR);
        gridPanel.add(emptyPanel);

        centerWrapper.add(gridPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // --- FOOTER (Çıkış Butonu) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(BG_COLOR);
        footerPanel.setBorder(new EmptyBorder(20, 0, 40, 0));

        JButton btnLogout = new JButton("Çıkış Yap");
        btnLogout.setPreferredSize(new Dimension(200, 50));
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            dispose();
            // GÜNCELLEME: Artık MainFrame değil, LoginPanel açılıyor
            new MainFrame().setVisible(true);
        });

        footerPanel.add(btnLogout);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    // --- ÖZEL BUTON TASARIMI ---
    private JPanel createMenuButton(String text, String icon, Color color, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 47, 51));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sol tarafta renk şeridi
        JPanel strip = new JPanel();
        strip.setBackground(color);
        strip.setPreferredSize(new Dimension(10, 0));
        panel.add(strip, BorderLayout.WEST);

        // İkon (Ortada, Büyük)
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setForeground(Color.WHITE);
        panel.add(lblIcon, BorderLayout.CENTER);

        // Yazı (Altta)
        JLabel lblText = new JLabel(text, SwingConstants.CENTER);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblText.setForeground(Color.WHITE);
        lblText.setBorder(new EmptyBorder(0, 0, 20, 0)); // Alttan boşluk
        panel.add(lblText, BorderLayout.SOUTH);

        // Tıklama Olayı
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(60, 63, 65)); // Hover rengi
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(44, 47, 51)); // Normal renk
            }
        });

        return panel;
    }
}