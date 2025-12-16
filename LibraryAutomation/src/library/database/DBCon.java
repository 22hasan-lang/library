package library.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBCon {
    private static final String URL = "jdbc:mysql://localhost:3306/library"; // veritabanı adı: library
    private static final String USER = "root";                               // kullanıcı adı
    private static final String PASSWORD = "";                               // şifre boşsa böyle bırak

    public static Connection connect() {
        System.out.println("🔄 Veritabanı bağlantısı deneniyor...");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Veritabanına bağlanıldı!");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ SQL Hatası: " + e.getMessage());
            return null;
        }
    }
}
