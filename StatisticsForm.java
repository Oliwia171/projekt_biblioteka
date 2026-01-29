import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StatisticsForm extends JFrame {
    public JPanel Panel1;
    private JPanel panelTytulu;
    private JPanel panelWykresu;
    private JPanel panelWykresWłasny;
    private JPanel panelLegenda;
    private JLabel lblwypozyczone;
    private JLabel lbldostepne;
    private JPanel paneltabeli;
    private JScrollPane Jscrollpanetabeli;
    private JTable tabelaStatystyk;
    private JButton odswiezButton;


    private Connection conn;

    public StatisticsForm() {
        initDatabase();
        initComponents();
        aktualizujDane();

        setContentPane(Panel1);
        setTitle("Statystyka");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void initDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/biblioteka", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Panel1, "Błąd połączenia z bazą: " + e.getMessage());
        }
    }

    private void initComponents() {

        panelWykresWłasny = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };

        panelWykresu.add(panelWykresWłasny);

        String[] kolumny = {"Stan książek", "Ilość", "Procent"};
        DefaultTableModel model = new DefaultTableModel(kolumny, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaStatystyk.setModel(model);

        tabelaStatystyk.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row == 0) c.setBackground(new Color(255, 200, 200));
                    else if (row == 1) c.setBackground(new Color(200, 255, 200));
                    else if (row == 2) c.setBackground(new Color(200, 200, 255));
                }
                return c;
            }
        });

        lblwypozyczone.setText(" Wypożyczone");
        lbldostepne.setText("Dostępne");


       odswiezButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aktualizujDane();
            }
        });

    }

    public void aktualizujDane() {
        try {
            int[] statystyki = getBookStatistics();
            int wypozyczone = statystyki[0];
            int dostepne = statystyki[1];
            int total = wypozyczone + dostepne;

            DefaultTableModel model = (DefaultTableModel) tabelaStatystyk.getModel();
            model.setRowCount(0);

            if (total > 0) {
                model.addRow(new Object[]{"Wypożyczone", wypozyczone, obliczProcent(wypozyczone, total)});
                model.addRow(new Object[]{"Dostępne", dostepne, obliczProcent(dostepne, total)});
            } else {
                model.addRow(new Object[]{"Wypożyczone", 0, "0%"});
                model.addRow(new Object[]{"Dostępne", 0, "0%"});
            }
            model.addRow(new Object[]{"RAZEM", total, "100%"});


            panelWykresWłasny.repaint();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(Panel1, "Błąd pobierania statystyk: " + e.getMessage());
        }
    }

    private int[] getBookStatistics() throws SQLException {
        int wypozyczone = 0;
        int dostepne = 0;

        String sql = "SELECT " +
                "SUM(CASE WHEN dostepna = 0 THEN 1 ELSE 0 END) as wypozyczone, " +
                "SUM(CASE WHEN dostepna = 1 THEN 1 ELSE 0 END) as dostepne " +
                "FROM books";

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            wypozyczone = rs.getInt("wypozyczone");
            dostepne = rs.getInt("dostepne");
        }

        return new int[]{wypozyczone, dostepne};
    }

    private String obliczProcent(int czesc, int calosc) {
        if (calosc == 0) return "0%";
        double procent = (czesc * 100.0) / calosc;
        return String.format("%.1f%%", procent);
    }

    private void drawChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int width = panelWykresWłasny.getWidth();
        int height = panelWykresWłasny.getHeight();


        DefaultTableModel model = (DefaultTableModel) tabelaStatystyk.getModel();
        if (model.getRowCount() < 2) return;

        int wypozyczone = 0;
        int dostepne = 0;

        try {
            wypozyczone = Integer.parseInt(model.getValueAt(0, 1).toString());
            dostepne = Integer.parseInt(model.getValueAt(1, 1).toString());
        } catch (Exception e) {
            return;
        }

        int total = wypozyczone + dostepne;

        if (total == 0) return;


        g2d.setColor(Color.RED);
        int circleSize = 100;
        g2d.fillOval(width/4 - circleSize/2, height/2 - circleSize/2, circleSize, circleSize);


        g2d.setColor(Color.GREEN);
        int squareSize = 100;
        g2d.fillRect(3*width/4 - squareSize/2, height/2 - squareSize/2, squareSize, squareSize);


        g2d.setColor(Color.BLACK);
        g2d.drawString("Wypożyczone: " + wypozyczone, width/4 - 40, height/2 + 70);
        g2d.drawString("Dostępne: " + dostepne, 3*width/4 - 40, height/2 + 70);
    }

    private void zamknijOkno() {
        Window window = SwingUtilities.getWindowAncestor(Panel1);
        if (window != null) {
            window.dispose();
        }
    }
}