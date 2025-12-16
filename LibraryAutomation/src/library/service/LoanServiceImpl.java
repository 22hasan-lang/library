package library.service;

import library.database.DBCon;
import library.model.Book;
import library.model.Loan;
import library.model.Member;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanServiceImpl implements ILoanService {

    private static final double DAILY_FINE = 1.0;     // Günlük gecikme cezası
    private static final int MIN_CREDIT = 80;         // Kitap almak için gereken minimum kredi

    // Not: Service'leri burada new'lemiyoruz, ihtiyaç duyulan metotta çağıracağız
    // aksi takdirde sonsuz döngü (StackOverflow) riski oluşabilir.

    // ----------------- Ödünç Alma -----------------
    @Override
    public boolean addLoan(Loan loan) {
        // 1. Üyenin kredisini kontrol et
        MemberServiceImpl memberService = new MemberServiceImpl();
        Member member = memberService.getMemberById(loan.getMember().getMemberId());

        if (member == null) return false;

        if (member.getCredit() < MIN_CREDIT) {
            System.out.println("❌ Yetersiz kredi: " + member.getCredit());
            return false;
        }

        // 2. Kitap zaten başkasında mı kontrol et (Extra Güvenlik)
        BookServiceImpl bookService = new BookServiceImpl();
        Book book = bookService.getBookById(loan.getBook().getBookId());
        if (book != null && !book.getStatus()) {
            System.out.println("❌ Kitap zaten ödünçte!");
            return false;
        }

        String insertLoanSql = "INSERT INTO loans (book_id, member_id, borrow_date, return_date, status, fine) VALUES (?, ?, ?, ?, ?, ?)";
        String updateBookSql = "UPDATE books SET status = 0 WHERE book_id = ?"; // 0 = False (Ödünçte)

        try (Connection conn = DBCon.connect()) {
            // Transaction başlatabiliriz ama basitlik adına düz işlem yapıyoruz.

            // A) Loans tablosuna ekle
            try (PreparedStatement pst = conn.prepareStatement(insertLoanSql)) {
                pst.setInt(1, loan.getBook().getBookId());
                pst.setInt(2, loan.getMember().getMemberId());
                pst.setDate(3, Date.valueOf(loan.getBorrowDate()));
                pst.setDate(4, null); // Yeni kayıtta iade tarihi olmaz
                pst.setString(5, "Borrowed");
                pst.setDouble(6, 0.0);
                pst.executeUpdate();
            }

            // B) Kitabın durumunu 'Unavailable' (0) yap
            try (PreparedStatement pstBook = conn.prepareStatement(updateBookSql)) {
                pstBook.setInt(1, loan.getBook().getBookId());
                pstBook.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ----------------- Kitap İade -----------------
    @Override
    public void returnBook(int loanId) {
        Loan loan = getLoanById(loanId); // Mevcut kaydı çek

        if (loan != null && "Borrowed".equals(loan.getStatus())) {
            LocalDate today = LocalDate.now();
            double fine = calculateFine(loanId); // Varsa ceza hesapla

            String updateLoanSql = "UPDATE loans SET return_date=?, status=?, fine=? WHERE loan_id=?";
            String updateBookSql = "UPDATE books SET status = 1 WHERE book_id=?"; // 1 = True (Available)
            String updateMemberSql = "UPDATE members SET credit=? WHERE member_id=?";

            try (Connection conn = DBCon.connect()) {
                // A) Loan tablosunu güncelle (İade edildi)
                try (PreparedStatement pst = conn.prepareStatement(updateLoanSql)) {
                    pst.setDate(1, Date.valueOf(today));
                    pst.setString(2, "Returned");
                    pst.setDouble(3, fine);
                    pst.setInt(4, loanId);
                    pst.executeUpdate();
                }

                // B) Kitabı tekrar 'Available' (1) yap
                try (PreparedStatement pstBook = conn.prepareStatement(updateBookSql)) {
                    pstBook.setInt(1, loan.getBook().getBookId());
                    pstBook.executeUpdate();
                }

                // C) Ceza varsa üye kredisinden düş
                if (fine > 0) {
                    Member member = loan.getMember();
                    // Member nesnesi o an null gelirse tekrar çekelim
                    if (member == null || member.getCredit() == 0) {
                        MemberServiceImpl ms = new MemberServiceImpl();
                        member = ms.getMemberById(loan.getMember().getMemberId());
                    }

                    if (member != null) {
                        int newCredit = (int) Math.max(0, member.getCredit() - fine);
                        try (PreparedStatement pstMem = conn.prepareStatement(updateMemberSql)) {
                            pstMem.setInt(1, newCredit);
                            pstMem.setInt(2, member.getMemberId());
                            pstMem.executeUpdate();
                        }
                    }
                }

                System.out.println("✅ Kitap iade işlemi tamamlandı.");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ----------------- Gecikme Cezası Hesaplama -----------------
    @Override
    public double calculateFine(int loanId) {
        Loan loan = getLoanById(loanId);
        if (loan == null) return 0.0;

        // Eğer zaten iade edildiyse, veritabanındaki cezayı döndür
        if (loan.getReturnDate() != null) {
            return loan.getFine();
        }

        // İade edilmemişse: Vade tarihi = Alış + 14 gün
        LocalDate dueDate = loan.getBorrowDate().plusDays(14);
        LocalDate checkDate = LocalDate.now();

        long daysLate = checkDate.toEpochDay() - dueDate.toEpochDay();
        return daysLate > 0 ? daysLate * DAILY_FINE : 0.0;
    }

    // ----------------- CRUD & Listeleme -----------------
    @Override
    public Loan getLoanById(int loanId) {
        String sql = "SELECT * FROM loans WHERE loan_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, loanId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY loan_id DESC"; // En son işlemler üstte
        try (Connection conn = DBCon.connect();
             Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    @Override
    public boolean updateLoan(Loan loan) {
        // Admin panelinden manuel güncelleme için
        String sql = "UPDATE loans SET book_id=?, member_id=?, borrow_date=?, return_date=?, status=?, fine=? WHERE loan_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, loan.getBook().getBookId());
            pst.setInt(2, loan.getMember().getMemberId());
            pst.setDate(3, Date.valueOf(loan.getBorrowDate()));
            // Return date null olabilir
            if (loan.getReturnDate() != null)
                pst.setDate(4, Date.valueOf(loan.getReturnDate()));
            else
                pst.setDate(4, null);

            pst.setString(5, loan.getStatus());
            pst.setDouble(6, loan.getFine());
            pst.setInt(7, loan.getLoanId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteLoan(int loanId) {
        // ÖNCE: Silinecek kaydı bulup hangi kitap olduğunu öğrenmeliyiz
        Loan loan = getLoanById(loanId);

        if (loan == null) {
            System.out.println("Silinecek kayıt bulunamadı.");
            return false;
        }

        String deleteSql = "DELETE FROM loans WHERE loan_id=?";
        String updateBookSql = "UPDATE books SET status = 1 WHERE book_id=?"; // 1 = Müsait

        try (Connection conn = DBCon.connect()) {

            // 1. ADIM: Kitabı tekrar müsait yap (Status = True)
            try (PreparedStatement pstBook = conn.prepareStatement(updateBookSql)) {
                pstBook.setInt(1, loan.getBook().getBookId());
                pstBook.executeUpdate();
            }

            // 2. ADIM: Borç kaydını sil
            try (PreparedStatement pstDelete = conn.prepareStatement(deleteSql)) {
                pstDelete.setInt(1, loanId);
                int affectedRows = pstDelete.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Kayıt silindi ve kitap boşa çıkarıldı.");
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Silme işlemi sırasında hata: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Loan> getLoansByMember(int memberId) {
        List<Loan> memberLoans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE member_id=? ORDER BY borrow_date DESC";
        try (Connection conn = DBCon.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, memberId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                memberLoans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberLoans;
    }

    @Override
    public List<Loan> searchLoans(String keyword) {
        return new ArrayList<>(); // UI tarafında manuel filtreleme yaptığın için burası boş kalabilir.
    }

    // ------------------ Helper: Veri Dönüştürme ------------------

    // ResultSet'ten gelen ham veriyi Loan nesnesine çevirir.
    // ÖNEMLİ: Book ve Member nesnelerini Service'lerden tam olarak çeker.
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        int loanId = rs.getInt("loan_id");
        int bookId = rs.getInt("book_id");
        int memberId = rs.getInt("member_id");

        Date borrowSql = rs.getDate("borrow_date");
        LocalDate borrowDate = borrowSql != null ? borrowSql.toLocalDate() : null;

        Date returnDateSql = rs.getDate("return_date");
        LocalDate returnDate = returnDateSql != null ? returnDateSql.toLocalDate() : null;

        String status = rs.getString("status");
        double fine = rs.getDouble("fine");

        // 1. Kitap Detaylarını Getir
        BookServiceImpl bookService = new BookServiceImpl();
        Book book = bookService.getBookById(bookId);
        // Eğer kitap silindiyse null dönebilir, UI patlamasın diye boş kitap objesi koyalım
        if (book == null) {
            book = new Book();
            book.setBookId(bookId);
            book.setBookName("Bilİnmiyor (Silinmiş)");
        }

        // 2. Üye Detaylarını Getir
        MemberServiceImpl memberService = new MemberServiceImpl();
        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            member = new Member();
            member.setMemberId(memberId);
            member.setMemberName("Bilinmiyor");
            member.setMemberSurname("(Silinmiş)");
        }

        return new Loan(loanId, book, member, borrowDate, returnDate, status, fine);
    }
}