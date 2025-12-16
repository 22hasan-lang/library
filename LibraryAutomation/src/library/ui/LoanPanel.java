package library.ui;

import library.model.Book;
import library.model.Loan;
import library.model.Member;
import library.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LoanPanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    // Componentler
    private JComboBox<Book> cmbBook;
    private JComboBox<Member> cmbMember;
    private JTextField txtBorrowDate, txtReturnDate, txtFine, searchField;
    private JComboBox<String> cmbStatus;

    // Servisler
    private ILoanService loanService = new LoanServiceImpl();
    private IBookService bookService = new BookServiceImpl();
    private IMemberService memberService = new MemberServiceImpl();

    // Tarih Formatı
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Renk Paleti
    private final Color BG_COLOR = new Color(30, 31, 38);
    private final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);

    public LoanPanel() {
        setTitle("Ödünç & İade Yönetimi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("📚 Ödünç & İade İşlemleri");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBounds(60, 30, 500, 40);
        mainPanel.add(titleLabel);

        // --- ARAMA ---
        JLabel searchLbl = new JLabel("🔍 Ara:");
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

        // Canlı Arama Listener
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchLoansRealTime(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchLoansRealTime(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchLoansRealTime(); }
        });

        // --- TABLO ---
        String[] cols = {"ID", "Kitap", "Üye", "Veriliş Tar.", "İade Tar.", "Durum", "Ceza (TL)"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);

        // Tablo Tasarımı
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(241, 196, 15)); // Sarı vurgu (Loan için)
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Hücreleri Ortala
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(60, 100, 1200, 400);
        scroll.getViewport().setBackground(new Color(44, 47, 51));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        mainPanel.add(scroll);

        // Tablo Seçim Olayı
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFieldsFromTable();
            }
        });

        // --- INPUT ALANLARI (FORM) ---
        int startX = 60;
        int startY = 540;
        int gapX = 400;
        int gapY = 50;

        // 1. Satır: Kitap, Üye, Veriliş Tarihi
        createLabel("Kitap Seç:", startX, startY, mainPanel);
        cmbBook = new JComboBox<>();
        cmbBook.setBounds(startX + 100, startY, 250, 35);
        mainPanel.add(cmbBook);

        createLabel("Üye Seç:", startX + gapX, startY, mainPanel);
        cmbMember = new JComboBox<>();
        cmbMember.setBounds(startX + gapX + 100, startY, 250, 35);
        mainPanel.add(cmbMember);

        txtBorrowDate = createModernField("Veriliş Tar:", startX + 2 * gapX, startY, mainPanel);
        txtBorrowDate.setText(LocalDate.now().format(dateFormatter)); // Otomatik bugün

        // 2. Satır: İade Tarihi, Durum, Ceza
        txtReturnDate = createModernField("İade Tar:", startX, startY + gapY, mainPanel);

        createLabel("Durum:", startX + gapX, startY + gapY, mainPanel);
        cmbStatus = new JComboBox<>(new String[]{"Borrowed", "Returned"});
        cmbStatus.setBounds(startX + gapX + 100, startY + gapY, 250, 35);
        mainPanel.add(cmbStatus);

        txtFine = createModernField("Ceza (TL):", startX + 2 * gapX, startY + gapY, mainPanel);
        txtFine.setText("0.0");
        txtFine.setEditable(false); // Elle ceza girilmez, hesaplanır

        // ComboBox'ları doldur
        loadComboBoxes();

        // --- BUTONLAR ---
        int btnY = 660;
        JButton addBtn = createModernButton("Ödünç Ver", new Color(46, 204, 113), 150, btnY);
        JButton updateBtn = createModernButton("Güncelle/İade", new Color(243, 156, 18), 320, btnY);
        JButton deleteBtn = createModernButton("Sil", new Color(231, 76, 60), 490, btnY);
        JButton listBtn = createModernButton("Yenile", new Color(52, 152, 219), 660, btnY);
        JButton backBtn = createModernButton("← Menü", new Color(149, 165, 166), 830, btnY);

        addBtn.addActionListener(e -> addLoan());
        updateBtn.addActionListener(e -> updateLoan());
        deleteBtn.addActionListener(e -> deleteLoan());
        listBtn.addActionListener(e -> { loadLoans(); loadComboBoxes(); });
        backBtn.addActionListener(e -> { dispose(); new MenuPanel().setVisible(true); });

        mainPanel.add(addBtn); mainPanel.add(updateBtn); mainPanel.add(deleteBtn); mainPanel.add(listBtn); mainPanel.add(backBtn);

        setVisible(true);
        loadLoans();
    }

    // --- YARDIMCI TASARIM METODLARI ---

    private void createLabel(String text, int x, int y, JPanel parent) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(x, y, 100, 30);
        parent.add(lbl);
    }

    private JTextField createModernField(String labelText, int x, int y, JPanel parent) {
        createLabel(labelText, x, y, parent);
        JTextField txt = new JTextField();
        txt.setBounds(x + 100, y, 250, 35);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(100, 100, 100)),
                new EmptyBorder(5, 5, 5, 5)));
        parent.add(txt);
        return txt;
    }

    private JButton createModernButton(String text, Color bg, int x, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 160, 45); // Butonları biraz genişlettim
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    // --- İŞ MANTIĞI (Önceki sağlam mantık korundu) ---

    private void loadComboBoxes() {
        cmbBook.removeAllItems();
        // Sadece müsait kitapları getir (Servis mantığı)
        for(Book b : bookService.getAvailableBooks()){
            cmbBook.addItem(b);
        }
        cmbMember.removeAllItems();
        for(Member m : memberService.getAllMembers()){
            cmbMember.addItem(m);
        }
        cmbBook.setSelectedIndex(-1);
        cmbMember.setSelectedIndex(-1);
    }

    private void loadLoans(){
        model.setRowCount(0);
        List<Loan> loans = loanService.getAllLoans();
        for(Loan l : loans){
            model.addRow(new Object[]{
                    l.getLoanId(),
                    l.getBook(),
                    l.getMember(),
                    l.getBorrowDate(),
                    l.getReturnDate(),
                    l.getStatus(),
                    l.getFine()
            });
        }
    }

    private void fillFieldsFromTable() {
        int row = table.getSelectedRow();

        // Not: Kitap listede yoksa (çünkü ödünçte), combo boş görünebilir.
        // Bunu düzeltmek için istersen geçici item ekleyebilirsin ama şimdilik mantığı bozmuyoruz.
        cmbBook.setSelectedItem(table.getValueAt(row, 1));
        cmbMember.setSelectedItem(table.getValueAt(row, 2));

        LocalDate bDate = (LocalDate) table.getValueAt(row, 3);
        LocalDate rDate = (LocalDate) table.getValueAt(row, 4);

        if (bDate != null) txtBorrowDate.setText(bDate.format(dateFormatter));
        if (rDate != null) txtReturnDate.setText(rDate.format(dateFormatter));
        else txtReturnDate.setText("");

        cmbStatus.setSelectedItem(table.getValueAt(row, 5));
        txtFine.setText(String.valueOf(table.getValueAt(row, 6)));
    }

    private void addLoan(){
        Book book = (Book)cmbBook.getSelectedItem();
        Member member = (Member)cmbMember.getSelectedItem();

        if(book == null || member == null){
            JOptionPane.showMessageDialog(this,"Lütfen kitap ve üye seçiniz!");
            return;
        }

        try {
            LocalDate borrowDate = LocalDate.parse(txtBorrowDate.getText().trim(), dateFormatter);
            LocalDate returnDate = (!txtReturnDate.getText().isEmpty())
                    ? LocalDate.parse(txtReturnDate.getText().trim(), dateFormatter) : null;
            String status = (String)cmbStatus.getSelectedItem();

            Loan loan = new Loan(book, member, borrowDate, status);
            loan.setReturnDate(returnDate);

            if(loanService.addLoan(loan)){
                JOptionPane.showMessageDialog(this,"Ödünç verildi!");
                resetFields();
            } else {
                JOptionPane.showMessageDialog(this,"İşlem başarısız (Kredi yetersiz veya kitap yok).");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tarih Formatı: gg.aa.yyyy olmalı!\n" + ex.getMessage());
        } finally {
            loadLoans();
            loadComboBoxes();
        }
    }

    private void updateLoan(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1){ JOptionPane.showMessageDialog(this,"Seçim yapınız!"); return; }

        try {
            int loanId = (int)table.getValueAt(selectedRow,0);

            // Kitap combobox'ta yoksa (çünkü ödünçte), tablodan al
            Book book = (Book)cmbBook.getSelectedItem();
            if(book == null) book = (Book)table.getValueAt(selectedRow, 1);

            Member member = (Member)cmbMember.getSelectedItem();
            LocalDate borrowDate = LocalDate.parse(txtBorrowDate.getText().trim(), dateFormatter);
            LocalDate returnDate = (!txtReturnDate.getText().isEmpty())
                    ? LocalDate.parse(txtReturnDate.getText().trim(), dateFormatter) : null;
            String status = (String)cmbStatus.getSelectedItem();
            double fine = Double.parseDouble(txtFine.getText());

            Loan loan = new Loan(loanId, book, member, borrowDate, returnDate, status, fine);

            // Eğer İade Edildi seçildiyse servisteki iade mantığını tetikle
            if ("Returned".equals(status)) {
                loanService.returnBook(loanId);
            } else {
                loanService.updateLoan(loan);
            }

            JOptionPane.showMessageDialog(this,"Güncellendi!");
            resetFields();
            loadLoans();
            loadComboBoxes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }

    private void deleteLoan(){
        int selectedRow = table.getSelectedRow();
        if(selectedRow == -1){ JOptionPane.showMessageDialog(this,"Seçim yapınız!"); return; }

        int loanId = (int)table.getValueAt(selectedRow, 0);
        if(JOptionPane.showConfirmDialog(this,"Silmek istediğine emin misin?","Onay",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
            if(loanService.deleteLoan(loanId)){
                JOptionPane.showMessageDialog(this,"Silindi!");
                resetFields();
            }
        }
        loadLoans();
        loadComboBoxes();
    }

    private void searchLoansRealTime() {
        String keyword = searchField.getText().trim().toLowerCase();
        model.setRowCount(0);
        List<Loan> loans = loanService.getAllLoans();
        for (Loan l : loans) {
            String bookName = (l.getBook() != null) ? l.getBook().getBookName().toLowerCase() : "";
            String memberName = (l.getMember() != null) ? (l.getMember().getMemberName() + " " + l.getMember().getMemberSurname()).toLowerCase() : "";

            if (bookName.contains(keyword) || memberName.contains(keyword)) {
                model.addRow(new Object[]{
                        l.getLoanId(), l.getBook(), l.getMember(),
                        l.getBorrowDate(), l.getReturnDate(), l.getStatus(), l.getFine()
                });
            }
        }
    }

    private void resetFields(){
        cmbBook.setSelectedIndex(-1);
        cmbMember.setSelectedIndex(-1);
        txtBorrowDate.setText(LocalDate.now().format(dateFormatter));
        txtReturnDate.setText("");
        txtFine.setText("0.0");
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
        loadComboBoxes();
    }
}