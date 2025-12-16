package library.database;

import library.database.DBCon;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🧠 Bağlantı testi başlıyor...");
        Connection conn = DBCon.connect();
        if (conn != null) {
            System.out.println("✅ Bağlantı başarılı!");
        } else {
            System.out.println("❌ Bağlantı başarısız!");
        }
    }
}
