package library.ui;

import library.model.Book;
import library.model.Loan;
import library.model.Member;
import library.service.BookServiceImpl;
import library.service.LoanServiceImpl;
import library.service.MemberServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StatisticsPanel extends JFrame {

    private BookServiceImpl bookService = new BookServiceImpl();
    private MemberServiceImpl memberService = new MemberServiceImpl();
    private LoanServiceImpl loanService = new LoanServiceImpl();

    public StatisticsPanel() {
        setTitle("📊 Library Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Ana Arka Plan
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 31, 38));
        setContentPane(mainPanel);

        // --- 1. HEADER (BAŞLIK) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(30, 31, 38));
        headerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel titleLabel = new JLabel("Kütüphane İstatistik Paneli");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- 2. DATA HAZIRLIĞI ---
        List<Book> books = bookService.getAllBooks();
        List<Member> members = memberService.getAllMembers();
        List<Loan> loans = loanService.getAllLoans();

        long activeLoans = loans.stream().filter(l -> "Borrowed".equalsIgnoreCase(l.getStatus())).count();
        long availableBooks = books.stream().filter(Book::status).count();
        long borrowedBooks = books.size() - availableBooks;

        // Gecikmiş kitaplar (Bugün itibariyle 14 günü geçmiş ve hala 'Borrowed' olanlar)
        long overdue = loans.stream()
                .filter(l -> "Borrowed".equalsIgnoreCase(l.getStatus()))
                .filter(l -> l.getBorrowDate().plusDays(14).isBefore(LocalDate.now()))
                .count();

        double totalFine = loans.stream().mapToDouble(Loan::getFine).sum();


        // --- 3. DASHBOARD GRID (IZGARA) ---
        // 4 Sütunlu, satır sayısı otomatik, aralarda 20px boşluk
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(new Color(30, 31, 38));
        gridPanel.setBorder(new EmptyBorder(20, 40, 20, 40)); // Kenarlardan boşluk

        // Kartları Ekleme
        gridPanel.add(createDashboardCard("Toplam Kitap", String.valueOf(books.size()), "📚", new Color(52, 152, 219)));
        gridPanel.add(createDashboardCard("Toplam Üye", String.valueOf(members.size()), "👥", new Color(46, 204, 113)));
        gridPanel.add(createDashboardCard("Toplam İşlem", String.valueOf(loans.size()), "📝", new Color(155, 89, 182)));
        gridPanel.add(createDashboardCard("Aktif Ödünç", String.valueOf(activeLoans), "⏳", new Color(241, 196, 15)));

        gridPanel.add(createDashboardCard("Raftaki Kitaplar", String.valueOf(availableBooks), "✅", new Color(39, 174, 96)));
        gridPanel.add(createDashboardCard("Dışarıdaki Kitaplar", String.valueOf(borrowedBooks), "🚫", new Color(231, 76, 60)));
        gridPanel.add(createDashboardCard("Geciken İadeler", String.valueOf(overdue), "⚠️", new Color(192, 57, 43)));
        gridPanel.add(createDashboardCard("Toplam Ceza", String.format("%.2f TL", totalFine), "💰", new Color(230, 126, 34)));

        // Kartlar ekranın ortasında dursun diye wrapper panele alıyoruz
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);


        // --- 4. FOOTER (GERİ BUTONU) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(30, 31, 38));
        footerPanel.setBorder(new EmptyBorder(20, 40, 30, 40));

        JButton backBtn = new JButton("← Ana Menüye Dön");
        backBtn.setPreferredSize(new Dimension(200, 50));
        backBtn.setBackground(new Color(149, 165, 166));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Efekti (Üzerine gelince renk değişsin)
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(127, 140, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(149, 165, 166));
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MenuPanel().setVisible(true);
        });

        footerPanel.add(backBtn);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // --- MODERN KART TASARIMI ---
    private JPanel createDashboardCard(String title, String value, String icon, Color barColor) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(44, 47, 51)); // Kart arka planı (Koyu gri)

        // Sol taraftaki renkli şerit
        JPanel colorBar = new JPanel();
        colorBar.setPreferredSize(new Dimension(10, 0));
        colorBar.setBackground(barColor);
        card.add(colorBar, BorderLayout.WEST);

        // İçerik Paneli
        JPanel contentPanel = new JPanel(new GridLayout(2, 1));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 20, 15, 10));

        // Başlık
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(new Color(189, 195, 199)); // Açık gri yazı

        // Değer
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(Color.WHITE);

        contentPanel.add(lblTitle);
        contentPanel.add(lblValue);
        card.add(contentPanel, BorderLayout.CENTER);

        // İkon (Sağ taraf)
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(new Color(255, 255, 255, 100)); // Hafif şeffaf
        lblIcon.setBorder(new EmptyBorder(0, 0, 0, 20));
        card.add(lblIcon, BorderLayout.EAST);

        // Hafif bir kenarlık ekleyelim
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        return card;
    }
}