import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private JLabel lblmenu;
    private JLabel lbtytul;
    private JButton książkiButton;
    private JButton użytkownicyButton;
    private JButton wypożyczeniaButton;
    private JButton zwrotyButton;
    private JButton statystykiButton;
    private JPanel Panel1;

    public MainWindow() {
        setContentPane(Panel1);
        setTitle("SYSTEM ZARZĄDZANIA BIBLIOTEKĄ");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        książkiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new BooksForm();
                });
            }
        });

        użytkownicyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new UsersForm();
                });
            }
        });

        wypożyczeniaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    new RentalsForm();
                });
            }
        });

        zwrotyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {

                 new ReturnForm();
                });
            }
        });

        statystykiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                  new StatisticsForm();
                });
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}