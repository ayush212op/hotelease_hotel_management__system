package hotelmanagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class GuestUI extends JFrame {

    private JTable guestTable;
    private DefaultTableModel model;

    private JTextField nameField;
    private JTextField phoneField;

    public GuestUI() {

        setTitle("HotelEase Guest Manager");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildTable();
        buildControlPanel();

        loadGuests();

        setVisible(true);
    }

    private void buildTable() {

        String[] columns = {"Guest ID","Name","Phone"};
        model = new DefaultTableModel(columns,0);
        guestTable = new JTable(model);

        JScrollPane scroll = new JScrollPane(guestTable);
        add(scroll, BorderLayout.CENTER);
    }

    private void buildControlPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(15);
        JLabel phoneLabel = new JLabel("Phone:");
        phoneField = new JTextField(15);

        JButton addBtn = new JButton("Add Guest");
        JButton deleteBtn = new JButton("Remove Guest");
        JButton refreshBtn = new JButton("Refresh");

        // Horizontal layout: Label + Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 2;
        panel.add(phoneLabel, gbc);

        gbc.gridx = 3;
        panel.add(phoneField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(addBtn, gbc);

        gbc.gridx = 1;
        panel.add(deleteBtn, gbc);

        gbc.gridx = 2;
        panel.add(refreshBtn, gbc);

        add(panel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addGuest());
        deleteBtn.addActionListener(e -> removeGuest());
        refreshBtn.addActionListener(e -> loadGuests());
    }

    private Connection getConnection() {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "DB_USER", "DB_PASS"
            );
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed:\n"+e.getMessage());
            return null;
        }
    }

    private void loadGuests() {

        model.setRowCount(0);

        try {
            Connection con = getConnection();
            if(con == null) return;

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM guests ORDER BY guest_id");

            while(rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("guest_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                });
            }

            con.close();

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading guests:\n"+e.getMessage());
        }
    }

    private void addGuest() {

        String name = nameField.getText();
        String phone = phoneField.getText();

        if(name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name and phone");
            return;
        }

        try {
            Connection con = getConnection();
            if(con == null) return;

            String query = "INSERT INTO guests VALUES (guest_seq.NEXTVAL,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, phone);

            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this,"Guest added successfully");
            loadGuests();

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding guest:\n"+e.getMessage());
        }
    }

    private void removeGuest() {

        int selectedRow = guestTable.getSelectedRow();
        if(selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Select a guest to remove");
            return;
        }

        int guestId = (int) model.getValueAt(selectedRow, 0);

        try {
            Connection con = getConnection();
            if(con == null) return;

            String query = "DELETE FROM guests WHERE guest_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, guestId);

            ps.executeUpdate();
            con.close();

            JOptionPane.showMessageDialog(this,"Guest removed successfully");
            loadGuests();

        } catch(Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error removing guest:\n"+e.getMessage());
        }
    }

    public static void main(String[] args) {

        try {
            FlatDraculaIJTheme.setup();
        } catch(Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(GuestUI::new);
    }
}