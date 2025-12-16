package library.service;

import library.model.Loan;

import java.sql.SQLException;
import java.util.List;

public interface ILoanService {
    // Yeni ödünç kaydı ekleme
    boolean addLoan(Loan loan);

    // ID ile ödünç kaydı bulma
    Loan getLoanById(int loanId);

    // Tüm ödünç kayıtları listeleme
    List<Loan> getAllLoans();

    // Ödünç kaydını güncelleme
    boolean updateLoan(Loan loan);

    // Ödünç kaydını silme
    boolean deleteLoan(int loanId);

    // Ödünç kaydını iade etme
    void returnBook(int loanId) throws SQLException;

    // Belirli bir üyenin ödünç aldığı tüm kitapları getirme
    List<Loan> getLoansByMember(int memberId);

    // Gecikme cezası hesaplama
    double calculateFine(int loanId);

    List<Loan> searchLoans(String keyword);
}
