package bookingclient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class ClientDashboard extends JFrame {

    private JTextField guestNameField, guestPhoneField;
    public JTable roomsTable;
    private DefaultTableModel tableModel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientDashboard(String username) {
        setTitle("HotelEase Booking Dashboard - " + username);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        buildTopPanel();
        buildTablePanel();

        connectServer();
        loadRooms();

        setVisible(true);
    }

    private void buildTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Guest Info"));

        topPanel.add(new JLabel("Guest Name:"));
        guestNameField = new JTextField(15);
        topPanel.add(guestNameField);

        topPanel.add(new JLabel("Phone:"));
        guestPhoneField = new JTextField(12);
        topPanel.add(guestPhoneField);

        JButton refreshBtn = new JButton("Refresh Rooms");
        refreshBtn.addActionListener(e -> loadRooms());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void buildTablePanel() {
        String[] columns = {"Room ID", "Type", "Price", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Action column editable
            }
        };
        roomsTable = new JTable(tableModel);
        roomsTable.setRowHeight(30);
        roomsTable.setFillsViewportHeight(true);
        roomsTable.setAutoCreateRowSorter(true);

        // Center/Right alignment for columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        roomsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Room ID
        roomsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Status
        roomsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {{ setHorizontalAlignment(JLabel.RIGHT); }}); // Price

        // Status column coloring
        roomsTable.getColumn("Status").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setForeground(value.toString().equalsIgnoreCase("AVAILABLE") ? Color.GREEN : Color.RED);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setToolTipText("Room " + table.getValueAt(row,0) + ": " + value);
            label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return label;
        });

        // Action column (book button)
        roomsTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        roomsTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scroll = new JScrollPane(roomsTable);
        add(scroll, BorderLayout.CENTER);
    }

    private void connectServer() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server connection failed:\n" + e.getMessage());
        }
    }

    public void loadRooms() {
        tableModel.setRowCount(0);
        try {
            out.println("LIST_ROOMS");
            String response = in.readLine();
            if(response != null && !response.isEmpty()) {
                for(String r : response.split(";")) {
                    String[] data = r.split(",");
                    int roomId = Integer.parseInt(data[0]);
                    tableModel.addRow(new Object[]{roomId, data[1], data[2], data[3], "Book"});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error loading rooms:\n"+e.getMessage());
        }
    }

    public void bookRoom(int roomId) {
        String name = guestNameField.getText();
        String phone = guestPhoneField.getText();
        if(name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Enter guest name and phone");
            return;
        }

        try {
            out.println("BOOK_ROOM:" + roomId + ":" + name + ":" + phone);
            JOptionPane.showMessageDialog(this, in.readLine());
            guestNameField.setText("");
            guestPhoneField.setText("");
            loadRooms();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Booking failed:\n"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        try { FlatDraculaIJTheme.setup(); } catch (Exception e){ e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new ClientDashboard("ayush"));
    }
}