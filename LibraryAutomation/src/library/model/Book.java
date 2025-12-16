package library.model; // Paketin: library.model

public class Book {

   private String bookName;
   private String author;
   private String publisher;
   private String ISBN;
   private int price;
   private int bookId;
   private Boolean status;
   private String category;
   public Book() {

   }
   public Book(String bookName, String author, String publisher, String ISBN, int price, int bookId, Boolean status, String category) {
       this.bookName = bookName;
       this.author = author;
       this.publisher = publisher;
       this.ISBN = ISBN;
       this.price = price;
       this.bookId = bookId;
       this.status = status;
       this.category = category;
   }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    // Book.java

    public Boolean status() {
    return status;
   }
    // Book.java dosyasının içine, en alta ekle:
    @Override
    public String toString() {
        // Ekranda sadece kitap adı görünür. İstersen yazarını da ekleyebilirsin.
        return bookName;
        // Eğer değişkenin adı 'title' ise: return title; yap.
    }
}