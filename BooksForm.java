import javax.swing.*;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BooksForm extends JFrame {

    public JPanel Panel1;
    public JTable TabelaKsiazek;
    public JTextField textFieldtytul;
    public JTextField textFieldautor;
    public JTextField textFieldgatunek;
    public JTextField textFieldrok;
    public JTextField textFieldID;
    public JButton dodajButton;
    public JButton usuńButton;
    public JTextField textField1;
    public JButton szukajButton;
    public JPanel Panel2;
    public JPanel Panel4;

    private TableRowSorter sorter;

    public BooksForm() {


        setContentPane(Panel1);
        setTitle("Zarządzanie Książkami");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        if (TabelaKsiazek == null) {
            System.err.println("Tabela książek jest pusta");
            TabelaKsiazek = new JTable();
        }
        TabelaKsiazek.setModel(DataStore.KSIAZKI_MODEL);
        TabelaKsiazek.setDefaultEditor(Object.class, null);

        sorter = new TableRowSorter<>(DataStore.KSIAZKI_MODEL);
        TabelaKsiazek.setRowSorter(sorter);


        if (textFieldID != null) {
            textFieldID.setText("auto");
            textFieldID.setEditable(false);
        }

        if (dodajButton != null) {
            dodajButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dodajKsiazke();
                }
            });
        }

        if (usuńButton != null) {
            usuńButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    usunKsiazke();
                }
            });
        }

        if (szukajButton != null) {
            szukajButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    szukajKsiazki();
                }
            });
        }


        DataStore.loadBooks();
        setVisible(true);
        System.out.println("BooksForm: Okno widoczne");
    }

    private void dodajKsiazke() {
        String tytul = textFieldtytul.getText().trim();
        String autor = textFieldautor.getText().trim();
        String gatunek = textFieldgatunek.getText().trim();
        String rokText = textFieldrok.getText().trim();

        if (tytul.isEmpty() || autor.isEmpty() || gatunek.isEmpty() || rokText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wypełnij wszystkie pola");
            return;
        }

        try {
            int rok = Integer.parseInt(rokText);

            int currentYear = java.time.Year.now().getValue();
            if (rok < 1000 || rok > currentYear) {
                JOptionPane.showMessageDialog(this,
                        "Podaj poprawny rok (1000-" + currentYear + " ");
                return;
            }

            boolean success = DataStore.addBook(tytul, autor, gatunek, rok);

            if (success) {
          
                textFieldtytul.setText("");
                textFieldautor.setText("");
                textFieldgatunek.setText("");
                textFieldrok.setText("");
                JOptionPane.showMessageDialog(this, "Książka dodana");
            } else {
                JOptionPane.showMessageDialog(this, "Błąd dodawania książki");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rok musi być liczbą!");
        }
    }

    private void usunKsiazke() {
        int row = TabelaKsiazek.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Zaznacz książkę");
            return;
        }

        int modelRow = TabelaKsiazek.convertRowIndexToModel(row);
        int id = (int) DataStore.KSIAZKI_MODEL.getValueAt(modelRow, 0);
        String title = (String) DataStore.KSIAZKI_MODEL.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Usunąć książkę:\n" + title + "?",
                "Potwierdzenie",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DataStore.deleteBook(id);

            if (!success) {
                JOptionPane.showMessageDialog(this,
                        "Nie można usunąć książki!\nKsiążka jest obecnie wypożyczona.",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void szukajKsiazki() {
        String text = textField1.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BooksForm();
        });
    }
}