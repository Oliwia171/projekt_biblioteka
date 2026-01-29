import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class RentalsForm extends JFrame {
    public JPanel Panel1;
    private JPanel Panelgora;
    private JTextField textFieldidczytelnika;
    private JTextField textFieldidksiazki;
    private JTextField textFielddata;
    private JButton wypożyczButton;
    private JButton anulujButton;
    private JPanel PanelCenter;
    private JTable table1;

    public RentalsForm() {

        setContentPane(Panel1);
        setTitle("Wypożyczenia");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        table1.setModel(DataStore.WYPOZYCZENIA_MODEL);
        table1.setDefaultEditor(Object.class, null);
        //Data
        textFielddata.setText(LocalDate.now().toString());
        textFielddata.setEditable(false);

        wypożyczButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String idCzytelnikaStr = textFieldidczytelnika.getText().trim();
                String idKsiazkiStr = textFieldidksiazki.getText().trim();

                if (idCzytelnikaStr.isEmpty() || idKsiazkiStr.isEmpty()) {
                    JOptionPane.showMessageDialog(Panel1,
                            "Wprowadź ID Czytelnika i ID Książki!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int userId = Integer.parseInt(idCzytelnikaStr);
                    int bookId = Integer.parseInt(idKsiazkiStr);

                    if (userId <= 0 || bookId <= 0) {
                        JOptionPane.showMessageDialog(Panel1,
                                "ID musi być dodatnie", "Błąd", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean success = DataStore.addRental(userId, bookId);

                    if (success) {
                        textFieldidczytelnika.setText("");
                        textFieldidksiazki.setText("");
                        JOptionPane.showMessageDialog(Panel1,
                                "Wypożyczenie zarejestrowane!");
                    } else {
                        JOptionPane.showMessageDialog(Panel1,
                                "Błąd wypożyczenia!\nSprawdź czy:\n" +
                                        "1. Użytkownik istnieje\n" +
                                        "2. Książka istnieje\n" +
                                        "3. Książka jest dostępna",
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Panel1,
                            "ID muszą być liczbami całkowitymi!", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        anulujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldidczytelnika.setText("");
                textFieldidksiazki.setText("");
            }
        });

        DataStore.loadRentals();
    }
}