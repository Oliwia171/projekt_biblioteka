public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver załadowany");
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika MySQL  Dodaj mysql-connector-j.jar");
            return;
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}