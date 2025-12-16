package library.model;

import java.time.LocalDate;

public class Loan {
    private int loanId;
    private Book book;          // İlişkili kitap
    private Member member;      // Ödünç alan üye
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;      // "Borrowed" veya "Returned"
    private double fine;

    // Boş constructor
    public Loan() {
    }

    // --- DÜZELTİLEN YER 1: Ekleme Constructor'ı ---
    // (Artık verileri içeriye kaydediyor)
    public Loan(Book book, Member member, LocalDate borrowDate, String status) {
        this.book = book;          // <--- BAK BURASI ÇOK ÖNEMLİ
        this.member = member;      // <--- BUNU YAZMAZSAN HATA VERİR
        this.borrowDate = borrowDate;
        this.status = status;
        this.fine = 0.0;
    }

    // --- DÜZELTİLEN YER 2: Güncelleme Constructor'ı ---
    public Loan(int loanId, Book book, Member member, LocalDate borrowDate, LocalDate returnDate, String status, double fine) {
        this.loanId = loanId;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }

    // --- Getter & Setter ---
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    // Ek metodlar (StatisticsPanel için lazım olabilir)
    public int getBookId() { return book != null ? book.getBookId() : -1; }
    public int getMemberId() { return member != null ? member.getMemberId() : -1; }

    // Hata ayıklarken konsolda okunaklı çıksın diye
    @Override
    public String toString() {
        return "Loan [ID=" + loanId + ", Book=" + getBook() + ", Member=" + getMember() + "]";
    }
}