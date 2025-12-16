package library.ui;

import library.model.Member;
import library.service.MemberServiceImpl;

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

public class MemberPanel extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtName, txtSurname, txtPhone, txtEmail, txtIdentityId, txtCredit, searchField;
    private MemberServiceImpl memberService = new MemberServiceImpl();

    private final Color BG_COLOR = new Color(30, 31, 38);
    private final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);

    public MemberPanel() {
        setTitle("👤 Üye Yönetimi");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        // --- BAŞLIK ---
        JLabel titleLabel = new JLabel("Üye Yönetimi");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setBounds(60, 30, 400, 40);
        mainPanel.add(titleLabel);

        // --- ARAMA ---
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
            public void insertUpdate(DocumentEvent e) { searchMembers(); }
            public void removeUpdate(DocumentEvent e) { searchMembers(); }
            public void changedUpdate(DocumentEvent e) { searchMembers(); }
        });

        // --- TABLO ---
        String[] cols = {"ID", "Ad", "Soyad", "Telefon", "Email", "TC No", "Kredi"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);

        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(46, 204, 113)); // Üyelerde yeşil vurgu
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Ortala
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

        // Seçim Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtName.setText((String) table.getValueAt(row, 1));
                txtSurname.setText((String) table.getValueAt(row, 2));
                txtPhone.setText((String) table.getValueAt(row, 3));
                txtEmail.setText((String) table.getValueAt(row, 4));
                txtIdentityId.setText((String) table.getValueAt(row, 5));
                txtCredit.setText(table.getValueAt(row, 6).toString());
            }
        });

        // --- INPUTLAR ---
        int startX = 60;
        int startY = 540;
        int gapX = 400;
        int gapY = 50;

        txtName = createModernField("Ad:", startX, startY, mainPanel);
        txtSurname = createModernField("Soyad:", startX + gapX, startY, mainPanel);
        txtPhone = createModernField("Telefon:", startX + 2 * gapX, startY, mainPanel);

        txtEmail = createModernField("Email:", startX, startY + gapY, mainPanel);
        txtIdentityId = createModernField("TC No:", startX + gapX, startY + gapY, mainPanel);
        txtCredit = createModernField("Kredi:", startX + 2 * gapX, startY + gapY, mainPanel);

        // --- BUTONLAR ---
        int btnY = 660;
        JButton addBtn = createModernButton("Ekle", new Color(46, 204, 113), 150, btnY);
        JButton updateBtn = createModernButton("Güncelle", new Color(243, 156, 18), 320, btnY);
        JButton deleteBtn = createModernButton("Sil", new Color(231, 76, 60), 490, btnY);
        JButton listBtn = createModernButton("Yenile", new Color(52, 152, 219), 660, btnY);
        JButton backBtn = createModernButton("← Menü", new Color(149, 165, 166), 830, btnY);

        addBtn.addActionListener(e -> { addMember(); resetFields(); });
        updateBtn.addActionListener(e -> { updateMember(); resetFields(); });
        deleteBtn.addActionListener(e -> { deleteMember(); resetFields(); });
        listBtn.addActionListener(e -> loadMembers());
        backBtn.addActionListener(e -> { dispose(); new MenuPanel().setVisible(true); });

        mainPanel.add(addBtn); mainPanel.add(updateBtn); mainPanel.add(deleteBtn); mainPanel.add(listBtn); mainPanel.add(backBtn);
        makeNumericOnly(txtIdentityId, 11); // TC No en fazla 11 hane ve sadece rakam
        makeNumericOnly(txtPhone, 11);      // Telefon en fazla 11 hane
        makeNumericOnly(txtCredit, 0);
        setVisible(true);
        loadMembers();
    }

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
                new EmptyBorder(5, 5, 5, 5)));
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
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void loadMembers() {
        model.setRowCount(0);
        List<Member> members = memberService.getAllMembers();
        for (Member m : members) {
            model.addRow(new Object[]{
                    m.getMemberId(), m.getMemberName(), m.getMemberSurname(),
                    m.getPhoneNumber(), m.getEmail(), m.getIdentityId(), m.getCredit()
            });
        }
    }

    private void addMember() {
        if (txtName.getText().isEmpty() || txtIdentityId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "İsim ve TC zorunludur!");
            return;
        }
        Member m = new Member();
        m.setMemberName(txtName.getText());
        m.setMemberSurname(txtSurname.getText());
        m.setPhoneNumber(txtPhone.getText());
        m.setEmail(txtEmail.getText());
        m.setIdentityId(txtIdentityId.getText());
        m.setCredit(Integer.parseInt(txtCredit.getText().isEmpty() ? "0" : txtCredit.getText()));

        if (memberService.addMember(m)) {
            JOptionPane.showMessageDialog(this, "Üye Eklendi!");
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(this, "Hata!");
        }
    }

    private void updateMember() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız!"); return; }

        Member m = new Member();
        m.setMemberId((int) table.getValueAt(row, 0));
        m.setMemberName(txtName.getText());
        m.setMemberSurname(txtSurname.getText());
        m.setPhoneNumber(txtPhone.getText());
        m.setEmail(txtEmail.getText());
        m.setIdentityId(txtIdentityId.getText());
        m.setCredit(Integer.parseInt(txtCredit.getText()));

        if (memberService.updateMember(m)) {
            JOptionPane.showMessageDialog(this, "Güncellendi!");
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(this, "Hata!");
        }
    }

    private void deleteMember() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Seçim yapınız!"); return; }
        int id = (int) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (memberService.deleteMember(id)) {
                JOptionPane.showMessageDialog(this, "Silindi!");
                loadMembers();
            } else { JOptionPane.showMessageDialog(this, "Hata!"); }
        }
    }

    private void searchMembers() {
        String keyword = searchField.getText().trim();
        model.setRowCount(0);
        List<Member> members = memberService.searchMembers(keyword);
        for (Member m : members) {
            model.addRow(new Object[]{
                    m.getMemberId(), m.getMemberName(), m.getMemberSurname(),
                    m.getPhoneNumber(), m.getEmail(), m.getIdentityId(), m.getCredit()
            });
        }
    }

    private void resetFields() {
        txtName.setText(""); txtSurname.setText(""); txtPhone.setText("");
        txtEmail.setText(""); txtIdentityId.setText(""); txtCredit.setText("");
    }

    private void makeNumericOnly(JTextField textField, int maxLength) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();

                // 1. Rakam değilse engelle (Backspace serbest)
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume(); // Tuşu yutar, yazılmasına izin vermez
                    return;
                }

                // 2. Uzunluk sınırını aşıyorsa engelle (maxLength 0 ise sınır yok)
                if (maxLength > 0 && textField.getText().length() >= maxLength && c != '\b') {
                    e.consume();
                }
            }
        });
    }

}