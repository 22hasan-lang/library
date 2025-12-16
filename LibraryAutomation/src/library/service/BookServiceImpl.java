package library.service;

import library.database.DBCon;
import library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookServiceImpl implements IBookService {

    @Override
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (book_name, author, publisher, isbn, price, status, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getISBN());
            ps.setInt(5, book.getPrice());
            ps.setBoolean(6, book.getStatus()); // true = Available, false = Borrowed
            ps.setString(7, book.getCategory());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET book_name=?, author=?, publisher=?, isbn=?, price=?, status=?, category=? WHERE book_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getBookName());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getISBN());
            ps.setInt(5, book.getPrice());
            ps.setBoolean(6, book.getStatus());
            ps.setString(7, book.getCategory());
            ps.setInt(8, book.getBookId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id=?";
        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getInt("price"),
                        rs.getInt("book_id"),
                        rs.getBoolean("status"),
                        rs.getString("category")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error fetching book by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DBCon.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getInt("price"),
                        rs.getInt("book_id"),
                        rs.getBoolean("status"),
                        rs.getString("category")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error listing books: " + e.getMessage());
        }

        return books;
    }

    // 🔹 Yeni metod: Aktif ödünç kitaplar
    @Override
    public List<Book> getActiveLoans() {
        List<Book> allBooks = getAllBooks();
        return allBooks.stream()
                .filter(book -> !book.getStatus()) // status=false -> ödünç, status=true -> mevcut
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE book_name LIKE ? OR author LIKE ? OR category LIKE ?";

        try (Connection conn = DBCon.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getInt("price"),
                        rs.getInt("book_id"),
                        rs.getBoolean("status"),
                        rs.getString("category")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error searching books: " + e.getMessage());
        }

        return books;
    }
    @Override
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();

        // GÜNCELLEME: Hem '1' olanları hem de yanlışlıkla 'NULL' girilmiş olanları getir
        String sql = "SELECT * FROM books WHERE status = 1 OR status IS NULL";

        try (Connection conn = DBCon.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("book_name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("isbn"),
                        rs.getInt("price"),
                        rs.getInt("book_id"),
                        rs.getBoolean("status"),
                        rs.getString("category")
                ));
            }
            // KONTROL İÇİN: Konsola kaç kitap bulduğunu yazdıralım
            System.out.println("Müsait kitap sayısı: " + books.size());

        } catch (SQLException e) {
            System.out.println("Müsait kitapları listelerken hata: " + e.getMessage());
        }
        return books;
    }
}
