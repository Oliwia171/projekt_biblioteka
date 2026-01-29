import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsersForm extends JFrame {
    public JPanel Panel1;
    private JTextField textField1;
    private JButton szukajButton;
    private JTable tabelaczytelnikow;
    private JTextField textFieldImie;
    private JTextField textFieldnazwisko;
    private JTextField textFieldemail;
    private JTextField textFieldidczytelnika;
    private JButton usuńButton;
    private JButton dodajButton;
    private JPanel Panel2;
    private JScrollPane scrollpane;
    private JPanel Panel3;
    private JPanel Panel4;

    private TableRowSorter<DefaultTableModel> sorter;

    public UsersForm() {
        setContentPane(Panel1);
        setTitle("Zarządzanie Czytelnikami");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabelaczytelnikow.setModel(DataStore.UZYTKOWNICY_MODEL);
        tabelaczytelnikow.setDefaultEditor(Object.class, null);

        sorter = new TableRowSorter<>(DataStore.UZYTKOWNICY_MODEL);
        tabelaczytelnikow.setRowSorter(sorter);

        textFieldidczytelnika.setText("auto");
        textFieldidczytelnika.setEditable(false);


        dodajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String imie = textFieldImie.getText().trim();
                String nazwisko = textFieldnazwisko.getText().trim();
                String email = textFieldemail.getText().trim();

                if (imie.isEmpty() || nazwisko.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(UsersForm.this,
                            "Wypełnij wszystkie pola!", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!email.contains("@") || !email.contains(".")) {
                    JOptionPane.showMessageDialog(UsersForm.this,
                            "Podaj poprawny adres email", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = DataStore.addUser(imie, nazwisko, email);

                if (success) {
                    textFieldImie.setText("");
                    textFieldnazwisko.setText("");
                    textFieldemail.setText("");
                    JOptionPane.showMessageDialog(UsersForm.this,
                            "Użytkownik dodany");
                } else {
                    JOptionPane.showMessageDialog(UsersForm.this,
                            "Użytkownik z takim emailem już istnieje",
                            "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        usuńButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = tabelaczytelnikow.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(UsersForm.this,
                            "Wybierz użytkownika do usunięcia", "Błąd", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int modelRow = tabelaczytelnikow.convertRowIndexToModel(row);
                int userId = (int) DataStore.UZYTKOWNICY_MODEL.getValueAt(modelRow, 0);
                String userName = (String) DataStore.UZYTKOWNICY_MODEL.getValueAt(modelRow, 1);
                String userLastName = (String) DataStore.UZYTKOWNICY_MODEL.getValueAt(modelRow, 2);

                int confirm = JOptionPane.showConfirmDialog(UsersForm.this,
                        "Czy na pewno chcesz usunąć użytkownika:\n" +
                                userName + " " + userLastName + "?",
                        "Potwierdzenie usunięcia",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = DataStore.deleteUser(userId);

                    if (success) {
                        JOptionPane.showMessageDialog(UsersForm.this,
                                "Użytkownik usunięty");
                    } else {
                        JOptionPane.showMessageDialog(UsersForm.this,
                                "Nie można usunąć użytkownika!\n" +
                                        "Użytkownik ma aktywne wypożyczenia.",
                                "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        szukajButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tekst = textField1.getText().trim();
                if (tekst.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + tekst));
                }
            }
        });

        DataStore.loadUsers();
        setVisible(true);
    }
}