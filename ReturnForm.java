import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReturnForm extends JFrame {
    public JPanel Panel1;
    private JTable tabelawypozyczen;
    private JScrollPane scrollpane;
    private JPanel paneltytulu;
    private JPanel paneldolny;
    private JPanel panelWyszukiwanie;
    private JTextField tfszukaj;
    private JButton btnSzukaj;
    private JPanel Panelprzyciski;
    private JButton btnzwroc;
    private JButton btnodswiez;
    private JButton btnZamknij;

    private TableRowSorter<DefaultTableModel> sorter;

    public ReturnForm() {
        setContentPane(Panel1);
        setTitle("Zwroty");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

        setupTable();
        setupListeners();


        DataStore.loadRentals();
    }

    private void setupTable() {

        tabelawypozyczen.setModel(DataStore.WYPOZYCZENIA_MODEL);
        tabelawypozyczen.setDefaultEditor(Object.class, null);
//WYSZUKIWANIE
        sorter = new TableRowSorter<>(DataStore.WYPOZYCZENIA_MODEL);
        tabelawypozyczen.setRowSorter(sorter);

        System.out.println("Model tabeli ustawiony: " + (tabelawypozyczen.getModel() == DataStore.WYPOZYCZENIA_MODEL));
        System.out.println(" Liczba wierszy w modelu: " + DataStore.WYPOZYCZENIA_MODEL.getRowCount());
    }

    private void setupListeners() {

        btnzwroc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zwrocKsiazke();
            }
        });


        btnodswiez.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DataStore.loadRentals();
                if (tfszukaj != null) tfszukaj.setText("");
                if (sorter != null) sorter.setRowFilter(null);
                JOptionPane.showMessageDialog(Panel1,
                        "Lista wypożyczeń odświeżona!");
            }
        });


        if (btnSzukaj != null) {
            btnSzukaj.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (tfszukaj != null && sorter != null) {
                        String tekst = tfszukaj.getText().trim();
                        if (tekst.isEmpty()) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + tekst));
                        }
                    }
                }
            });
        }


        if (btnZamknij != null) {
            btnZamknij.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(Panel1);
                    if (frame != null) {
                        frame.dispose();
                    }
                }
            });
        }


        if (tfszukaj != null) {
            tfszukaj.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (btnSzukaj != null) {
                        btnSzukaj.doClick();
                    }
                }
            });
        }
    }

    private void zwrocKsiazke() {
        int row = tabelawypozyczen.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(Panel1,
                    "Wybierz wypożyczenie do zwrotu");
            return;
        }

        int modelRow = tabelawypozyczen.convertRowIndexToModel(row);
        int rentalId = (int) DataStore.WYPOZYCZENIA_MODEL.getValueAt(modelRow, 0);
        String bookTitle = (String) DataStore.WYPOZYCZENIA_MODEL.getValueAt(modelRow, 5);
        String userName = (String) DataStore.WYPOZYCZENIA_MODEL.getValueAt(modelRow, 2);
        String userLastName = (String) DataStore.WYPOZYCZENIA_MODEL.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(Panel1,
                "Czy chcesz zwrócić tą książkę?\n" +
                        "Książka: " + bookTitle + "\n" +
                        "Czytelnik: " + userName + " " + userLastName,
                "Potwierdzenie zwrotu",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DataStore.returnBook(rentalId);

            if (success) {
                JOptionPane.showMessageDialog(Panel1,
                        "Książka " + bookTitle + " została zwrócona");
                DataStore.loadRentals(); // odśwież tabelę
            } else {
                JOptionPane.showMessageDialog(Panel1,
                        "Błąd podczas zwracania",
                        "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}