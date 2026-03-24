package hotelmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class RoomUI extends JFrame {

    private JTable roomTable;
    private DefaultTableModel model;

    public RoomUI(){

        setTitle("HotelEase Room Manager");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buildTable();
        buildControlPanel();

        loadRooms();

        setVisible(true);
    }

    private void buildTable(){

        String[] columns = {"Room ID","Room Type","Price","Status"};

        model = new DefaultTableModel(columns,0);
        roomTable = new JTable(model);

        JScrollPane scroll = new JScrollPane(roomTable);

        add(scroll,BorderLayout.CENTER);
    }

    private void buildControlPanel(){

        JPanel panel = new JPanel(new FlowLayout());

        JButton bookBtn = new JButton("Book Room");
        JButton checkoutBtn = new JButton("Checkout");
        JButton refreshBtn = new JButton("Refresh");

        bookBtn.addActionListener(e -> bookRoom());
        checkoutBtn.addActionListener(e -> checkoutRoom());
        refreshBtn.addActionListener(e -> loadRooms());

        panel.add(bookBtn);
        panel.add(checkoutBtn);
        panel.add(refreshBtn);

        add(panel,BorderLayout.SOUTH);
    }

    private Connection getConnection(){

        try{

            Class.forName("oracle.jdbc.driver.OracleDriver");

            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "system",
                    "system"
            );

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Database connection failed:\n"+e.getMessage());

            return null;
        }
    }

    private void loadRooms(){

        model.setRowCount(0);

        try{

            Connection con = getConnection();
            if(con == null) return;

            String query = "SELECT * FROM rooms";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getInt("price"),
                        rs.getString("status")
                });
            }

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Error loading rooms:\n"+e.getMessage());
        }
    }

    private void bookRoom(){

        int row = roomTable.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select a room first");
            return;
        }

        int roomId = (int) model.getValueAt(row,0);
        String status = (String) model.getValueAt(row,3);

        if(status.equals("BOOKED")){
            JOptionPane.showMessageDialog(this,"Room already booked");
            return;
        }

        try{

            Connection con = getConnection();
            if(con == null) return;

            String query = "UPDATE rooms SET status='BOOKED' WHERE room_id=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,roomId);

            ps.executeUpdate();
            con.close();

            loadRooms();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Error booking room:\n"+e.getMessage());
        }
    }

    private void checkoutRoom(){

        int row = roomTable.getSelectedRow();

        if(row == -1){
            JOptionPane.showMessageDialog(this,"Select a room first");
            return;
        }

        int roomId = (int) model.getValueAt(row,0);

        try{

            Connection con = getConnection();
            if(con == null) return;

            String query = "UPDATE rooms SET status='Available' WHERE room_id=?";

            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,roomId);

            ps.executeUpdate();
            con.close();

            loadRooms();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Error during checkout:\n"+e.getMessage());
        }
    }

    public static void main(String[] args){

        try{
            FlatDraculaIJTheme.setup();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(RoomUI::new);
    }
}