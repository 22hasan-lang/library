package library.service;

import library.model.Book;
import java.util.List;

public interface IBookService {

    boolean addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int bookId);
    Book getBookById(int bookId);
    List<Book> getAllBooks();
    List<Book> getAvailableBooks();
    // 🔹 Yeni metod: Aktif ödünç kitaplar
    List<Book> getActiveLoans();

    List<Book> searchBooks(String keyword);
}
