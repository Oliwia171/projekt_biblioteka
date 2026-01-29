import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DataStore {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/biblioteka?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static final DefaultTableModel KSIAZKI_MODEL =
            new DefaultTableModel(new Object[]{"ID", "Tytuł", "Autor", "Gatunek", "Rok", "Dostępna"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    public static final DefaultTableModel UZYTKOWNICY_MODEL =
            new DefaultTableModel(new Object[]{"ID", "Imię", "Nazwisko", "Email"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    public static final DefaultTableModel WYPOZYCZENIA_MODEL =
            new DefaultTableModel(new Object[]{
                    "ID", "ID Czytelnika", "Imię", "Nazwisko",
                    "ID Książki", "Tytuł", "Data"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    static {
        initDatabase();
    }

    private static void initDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                    USERNAME, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                stmt.execute("CREATE DATABASE IF NOT EXISTS biblioteka");
                stmt.execute("USE biblioteka");

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS books (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        tytul VARCHAR(100),
                        autor VARCHAR(100),
                        gatunek VARCHAR(50),
                        rok INT,
                        dostepna BOOLEAN DEFAULT TRUE
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        imie VARCHAR(50),
                        nazwisko VARCHAR(50),
                        email VARCHAR(100) UNIQUE
                    )
                """);

                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS rentals (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT,
                        book_id INT,
                        data DATE,
                        FOREIGN KEY (user_id) REFERENCES users(id),
                        FOREIGN KEY (book_id) REFERENCES books(id)
                    )
                """);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }

    public static void loadBooks() {
        KSIAZKI_MODEL.setRowCount(0);
        String sql = "SELECT * FROM books ORDER BY id";

        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                KSIAZKI_MODEL.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("tytul"),
                        rs.getString("autor"),
                        rs.getString("gatunek"),
                        rs.getInt("rok"),
                        rs.getBoolean("dostepna") ? "Tak" : "Nie"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addBook(String t, String a, String g, int r) {
        String sql = "INSERT INTO books (tytul, autor, gatunek, rok) VALUES (?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, t);
            ps.setString(2, a);
            ps.setString(3, g);
            ps.setInt(4, r);
            ps.executeUpdate();
            loadBooks();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean deleteBook(int id) {
        String check = "SELECT id FROM rentals WHERE book_id=?";
        String del = "DELETE FROM books WHERE id=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(check)) {

            ps.setInt(1, id);
            if (ps.executeQuery().next()) return false;

            PreparedStatement d = c.prepareStatement(del);
            d.setInt(1, id);
            d.executeUpdate();
            loadBooks();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static void loadUsers() {
        UZYTKOWNICY_MODEL.setRowCount(0);

        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM users ORDER BY id")) {

            while (rs.next()) {
                UZYTKOWNICY_MODEL.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String i, String n, String e) {
        String sql = "INSERT INTO users (imie, nazwisko, email) VALUES (?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, i);
            ps.setString(2, n);
            ps.setString(3, e);
            ps.executeUpdate();
            loadUsers();
            return true;

        } catch (SQLException ex) {
            return false;
        }
    }

    public static boolean deleteUser(int id) {
        String check = "SELECT id FROM rentals WHERE user_id=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(check)) {

            ps.setInt(1, id);
            if (ps.executeQuery().next()) return false;

            PreparedStatement d = c.prepareStatement("DELETE FROM users WHERE id=?");
            d.setInt(1, id);
            d.executeUpdate();
            loadUsers();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static void loadRentals() {
        WYPOZYCZENIA_MODEL.setRowCount(0);

        String sql = """
            SELECT r.id, r.user_id, u.imie, u.nazwisko,
                   r.book_id, b.tytul, r.data
            FROM rentals r
            JOIN users u ON r.user_id=u.id
            JOIN books b ON r.book_id=b.id
            ORDER BY r.data DESC
        """;

        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                WYPOZYCZENIA_MODEL.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getInt("book_id"),
                        rs.getString("tytul"),
                        rs.getDate("data")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addRental(int userId, int bookId) {
        try (Connection c = getConnection()) {

            PreparedStatement u = c.prepareStatement("SELECT id FROM users WHERE id=?");
            u.setInt(1, userId);
            if (!u.executeQuery().next()) return false;

            PreparedStatement b = c.prepareStatement("SELECT dostepna FROM books WHERE id=?");
            b.setInt(1, bookId);
            ResultSet br = b.executeQuery();
            if (!br.next() || !br.getBoolean("dostepna")) return false;

            PreparedStatement ins = c.prepareStatement(
                    "INSERT INTO rentals (user_id, book_id, data) VALUES (?,?,CURDATE())");
            ins.setInt(1, userId);
            ins.setInt(2, bookId);
            ins.executeUpdate();

            PreparedStatement upd = c.prepareStatement(
                    "UPDATE books SET dostepna=FALSE WHERE id=?");
            upd.setInt(1, bookId);
            upd.executeUpdate();

            loadRentals();
            loadBooks();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean returnBook(int rentalId) {
        try (Connection c = getConnection()) {

            PreparedStatement g = c.prepareStatement(
                    "SELECT book_id FROM rentals WHERE id=?");
            g.setInt(1, rentalId);
            ResultSet rs = g.executeQuery();
            if (!rs.next()) return false;

            int bookId = rs.getInt("book_id");

            PreparedStatement d = c.prepareStatement(
                    "DELETE FROM rentals WHERE id=?");
            d.setInt(1, rentalId);
            d.executeUpdate();

            PreparedStatement u = c.prepareStatement(
                    "UPDATE books SET dostepna=TRUE WHERE id=?");
            u.setInt(1, bookId);
            u.executeUpdate();

            loadRentals();
            loadBooks();
            return true;

        } catch (SQLException e) {
            return false;
        }
    }
}
