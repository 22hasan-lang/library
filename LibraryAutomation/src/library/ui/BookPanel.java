package library.ui;

import library.model.Book;
import library.service.BookServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BookPanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTitle, txtAuthor, txtISBN, txtPublisher, txtPrice, txtCategory, searchField;
    private BookServiceImpl bookService = new BookServiceImpl();

    // Renk Paleti
    private final Color BG_COLOR = new Color(30, 31, 38);
    private final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);
    private final Color ACCENT_COLOR = new Color(52, 152, 219);

    public BookPanel() {
        setTitle("📘 Kitap Yönetimi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(null); // Absolute Layout (Senin yapını korudum)
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("Kitap Yönetimi");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBounds(60, 30, 400, 40);
        mainPanel.add(titleLabel);

        // --- ARAMA ÇUBUĞU ---
        JLabel searchLbl = new JLabel("Ara:");
        searchLbl.setForeground(Color.LIGHT_GRAY);
        searchLbl.setBounds(850, 40, 50, 30);
        mainPanel.add(searchLbl);

        searchField = new JTextField();
        searchField.setBounds(900, 40, 300, 35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 5, 5, 5)));
        mainPanel.add(searchField);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchBooks(); }
            public void removeUpdate(DocumentEvent e) { searchBooks(); }
            public void changedUpdate(DocumentEvent e) { searchBooks(); }
        });

        // --- TABLO ---
        String[] cols = {"ID", "Kitap Adı", "Yazar", "ISBN", "Yayınevi", "Fiyat", "Kategori", "Durum"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);

        // Tablo Tasarımı
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        // Header Tasarımı
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Tablo verilerini ortalamak için renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(60, 100, 1200, 400);
        scroll.getViewport().setBackground(new Color(44, 47, 51)); // Tablo boşken arka plan
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        mainPanel.add(scroll);

        // Tablodan seçim yapınca alanları doldur
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtTitle.setText((String) table.getValueAt(row, 1));
                txtAuthor.setText((String) table.getValueAt(row, 2));
                txtISBN.setText((String) table.getValueAt(row, 3));
                txtPublisher.setText((String) table.getValueAt(row, 4));
                txtPrice.setText(table.getValueAt(row, 5).toString());
                txtCategory.setText((String) table.getValueAt(row, 6));
            }
        });

        // --- INPUT ALANLARI ---
        int startX = 60;
        int startY = 540;
        int gapX = 400; // Sütunlar arası boşluk
        int gapY = 50;  // Satırlar arası boşluk

        // 1. Satır
        txtTitle = createModernField("Kitap Adı:", startX, startY, mainPanel);
        txtAuthor = createModernField("Yazar:", startX + gapX, startY, mainPanel);
        txtISBN = createModernField("ISBN:", startX + 2 * gapX, startY, mainPanel);

        // 2. Satır
        txtPublisher = createModernField("Yayınevi:", startX, startY + gapY, mainPanel);
        txtPrice = createModernField("Fiyat:", startX + gapX, startY + gapY, mainPanel);
        txtCategory = createModernField("Kategori:", startX + 2 * gapX, startY + gapY, mainPanel);

        // --- BUTONLAR ---
        int btnY = 660;
        JButton addBtn = createModernButton("Ekle", new Color(46, 204, 113), 150, btnY);
        JButton updateBtn = createModernButton("Güncelle", new Color(243, 156, 18), 320, btnY);
        JButton deleteBtn = createModernButton("Sil", new Color(231, 76, 60), 490, btnY);
        JButton listBtn = createModernButton("Yenile", new Color(52, 152, 219), 660, btnY);
        JButton backBtn = createModernButton("← Menü", new Color(149, 165, 166), 830, btnY);

        addBtn.addActionListener(e -> { addBook(); resetFields(); });
        updateBtn.addActionListener(e -> { updateBook(); resetFields(); });
        deleteBtn.addActionListener(e -> { deleteBook(); resetFields(); });
        listBtn.addActionListener(e -> loadBooks());
        backBtn.addActionListener(e -> { dispose(); new MenuPanel().setVisible(true); });

        mainPanel.add(addBtn); mainPanel.add(updateBtn); mainPanel.add(deleteBtn); mainPanel.add(listBtn); mainPanel.add(backBtn);
        makeNumericOnly(txtPrice, 0); // Fiyat sadece rakam olsun
        makeNumericOnly(txtISBN, 13); // ISBN genelde 13 hane olur
        setVisible(true);
        loadBooks();
    }

    // --- YARDIMCI METODLAR (Tasarım İçin) ---

    private JTextField createModernField(String labelText, int x, int y, JPanel parent) {
        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(x, y, 100, 30);
        parent.add(lbl);

        JTextField txt = new JTextField();
        txt.setBounds(x + 90, y, 250, 35);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 5, 5, 5))); // İç boşluk (padding)
        parent.add(txt);
        return txt;
    }

    private JButton createModernButton(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 150, 45);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Efekti
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // --- İŞ MANTIĞI (Logic değişmedi) ---

    private void loadBooks() {
        model.setRowCount(0);
        List<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            model.addRow(new Object[]{
                    b.getBookId(), b.getBookName(), b.getAuthor(), b.getISBN(),
                    b.getPublisher(), b.getPrice(), b.getCategory(),
                    b.getStatus() ? "Müsait" : "Ödünçte"
            });
        }
    }

    private void addBook() {
        if (txtTitle.getText().isEmpty() || txtAuthor.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kitap adı ve Yazar zorunludur!");
            return;
        }
        Book b = new Book();
        b.setBookName(txtTitle.getText());
        b.setAuthor(txtAuthor.getText());
        b.setISBN(txtISBN.getText());
        b.setPublisher(txtPublisher.getText());
        b.setPrice(Integer.parseInt(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText()));
        b.setCategory(txtCategory.getText());
        b.setStatus(true);

        if (bookService.addBook(b)) {
            JOptionPane.showMessageDialog(this, "Kitap eklendi!");
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Hata oluştu!");
        }
    }

    private void updateBook() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen bir kitap seçin!"); return; }

        Book b = new Book();
        b.setBookId((int) table.getValueAt(row, 0));
        b.setBookName(txtTitle.getText());
        b.setAuthor(txtAuthor.getText());
        b.setISBN(txtISBN.getText());
        b.setPublisher(txtPublisher.getText());
        b.setPrice(Integer.parseInt(txtPrice.getText()));
        b.setCategory(txtCategory.getText());
        b.setStatus(true);

        if (bookService.updateBook(b)) {
            JOptionPane.showMessageDialog(this, "Güncellendi!");
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Hata!");
        }
    }

    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Lütfen bir kitap seçin!"); return; }

        int id = (int) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (bookService.deleteBook(id)) {
            JOptionPane.showMessageDialog(this, "Silindi!");
            loadBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Hata!");
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().trim();
        model.setRowCount(0);
        List<Book> books = bookService.searchBooks(keyword);
        for (Book b : books) {
            model.addRow(new Object[]{
                    b.getBookId(), b.getBookName(), b.getAuthor(), b.getISBN(),
                    b.getPublisher(), b.getPrice(), b.getCategory(),
                    b.getStatus() ? "Müsait" : "Ödünçte"
            });
        }
    }

    private void resetFields() {
        txtTitle.setText(""); txtAuthor.setText(""); txtISBN.setText("");
        txtPublisher.setText(""); txtPrice.setText(""); txtCategory.setText("");
    }

    private void makeNumericOnly(JTextField textField, int maxLength) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume();
                }
                if (maxLength > 0 && textField.getText().length() >= maxLength && c != '\b') {
                    e.consume();
                }
            }
        });
    }

}