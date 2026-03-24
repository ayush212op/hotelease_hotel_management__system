package hotelmanagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class DashboardUI extends JFrame {

    // Summary labels
    private JLabel totalRoomsLabel;
    private JLabel availableRoomsLabel;
    private JLabel bookedRoomsLabel;
    private JLabel totalGuestsLabel;

    public DashboardUI() {

        setTitle("HotelEase Dashboard");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildUI();
        loadSummary();

        setVisible(true);
    }

    private void buildUI() {
        // Title Panel
        JLabel title = new JLabel("HotelEase Admin Dashboard", JLabel.CENTER);
        title.setFont(new Font("Dancing Script", Font.PLAIN, 45));
        title.setForeground(new Color(189, 147, 249));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        totalRoomsLabel = new JLabel("Total Rooms: 0", JLabel.CENTER);
        availableRoomsLabel = new JLabel("Available Rooms: 0", JLabel.CENTER);
        bookedRoomsLabel = new JLabel("Booked Rooms: 0", JLabel.CENTER);
        totalGuestsLabel = new JLabel("Total Guests: 0", JLabel.CENTER);

        Font summaryFont = new Font("SansSerif", Font.BOLD, 22);
        totalRoomsLabel.setFont(summaryFont);
        availableRoomsLabel.setFont(summaryFont);
        bookedRoomsLabel.setFont(summaryFont);
        totalGuestsLabel.setFont(summaryFont);

        summaryPanel.add(totalRoomsLabel);
        summaryPanel.add(availableRoomsLabel);
        summaryPanel.add(bookedRoomsLabel);
        summaryPanel.add(totalGuestsLabel);

        add(summaryPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton roomBtn = new JButton("Manage Rooms");
        JButton guestBtn = new JButton("Manage Guests");
        JButton logoutBtn = new JButton("Logout");
        JButton exitBtn = new JButton("Exit");

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);
        roomBtn.setFont(buttonFont);
        guestBtn.setFont(buttonFont);
        logoutBtn.setFont(buttonFont);
        exitBtn.setFont(buttonFont);

        buttonPanel.add(roomBtn);
        buttonPanel.add(guestBtn);
        buttonPanel.add(logoutBtn);
        buttonPanel.add(exitBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        roomBtn.addActionListener(e -> new RoomUI());
        guestBtn.addActionListener(e -> new GuestUI());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI();
        });
        exitBtn.addActionListener(e -> System.exit(0));
    }

    private Connection getConnection() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "DB_USER", "DB_PASS"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed:\n" + e.getMessage());
            return null;
        }
    }

    private void loadSummary() {
        try {
            Connection con = getConnection();
            if (con == null) return;

            // Rooms Summary
            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM rooms");
            if (rs.next()) totalRoomsLabel.setText("Total Rooms: " + rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM rooms WHERE status='Available'");
            if (rs.next()) availableRoomsLabel.setText("Available Rooms: " + rs.getInt(1));

            rs = st.executeQuery("SELECT COUNT(*) FROM rooms WHERE status='BOOKED'");
            if (rs.next()) bookedRoomsLabel.setText("Booked Rooms: " + rs.getInt(1));

            // Guests Summary
            rs = st.executeQuery("SELECT COUNT(*) FROM guests");
            if (rs.next()) totalGuestsLabel.setText("Total Guests: " + rs.getInt(1));

            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading summary:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            FlatDraculaIJTheme.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(DashboardUI::new);
    }
}