import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public TablePanel() {
        setLayout(new BorderLayout());

        // Kreiranje modela tabele
        tableModel = new DefaultTableModel(new String[]{"Kolona1", "Kolona2", "Kolona3"}, 0);
        table = new JTable(tableModel);

        // Dodavanje JScrollPane za tabelu
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Dugme za dodavanje novog reda
        JButton addButton = new JButton("Dodaj red");
        add(addButton, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow(new Object[]{"", "", ""});
            }
        });
    }

    // Metoda za unos podataka u bazu podataka
    public void saveToDatabase(String dbUrl, String username, String password) {
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
            String sql = "INSERT INTO your_table_name (column1, column2, column3) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    stmt.setString(1, tableModel.getValueAt(i, 0).toString());
                    stmt.setString(2, tableModel.getValueAt(i, 1).toString());
                    stmt.setString(3, tableModel.getValueAt(i, 2).toString());
                    stmt.addBatch(); // Dodaj u batch
                }
                stmt.executeBatch(); // Izvrši batch
                JOptionPane.showMessageDialog(this, "Podaci su uspešno uneti u bazu!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Došlo je do greške prilikom unosa u bazu: " + ex.getMessage());
        }
    }

    // Testiranje panela
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tabela sa unosom");
        TablePanel panel = new TablePanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setVisible(true);

        // Dugme za cuvanje podataka u bazu
        JButton saveButton = new JButton("Sacuvaj u bazu");
        panel.add(saveButton, BorderLayout.NORTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dbUrl = "jdbc:mysql://localhost:3306/your_database_name";
                String username = "your_username";
                String password = "your_password";
                panel.saveToDatabase(dbUrl, username, password);
            }
        });
    }
}
