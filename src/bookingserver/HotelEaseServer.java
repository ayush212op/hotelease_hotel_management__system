package bookingserver;

import java.io.*;
import java.net.*;
import java.sql.*;

public class HotelEaseServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("HotelEase Server starting on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
                new ClientHandler(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "DB_USER", "DB_PASS"
        );
    }

    private static class ClientHandler extends Thread {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket client) throws IOException {
            this.client = client;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                String request;
                while ((request = in.readLine()) != null) {
                    System.out.println("Request: " + request);
                    handleRequest(request);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { client.close(); } catch (IOException ignored) {}
            }
        }

        private void handleRequest(String request) {
            try (Connection con = getConnection()) {

                if (request.equalsIgnoreCase("LIST_ROOMS")) {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT room_id, room_type, price, status FROM rooms");
                    StringBuilder response = new StringBuilder();
                    while (rs.next()) {
                        response.append(rs.getInt("room_id")).append(",")
                                .append(rs.getString("room_type")).append(",")
                                .append(rs.getInt("price")).append(",")
                                .append(rs.getString("status")).append(";");
                    }
                    out.println(response.toString());

                } else if (request.startsWith("BOOK_ROOM:")) {
                    String[] parts = request.split(":");
                    int roomId = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    String phone = parts[3];

                    // Check current status
                    PreparedStatement psCheck = con.prepareStatement("SELECT status FROM rooms WHERE room_id=?");
                    psCheck.setInt(1, roomId);
                    ResultSet rs = psCheck.executeQuery();

                    if (rs.next() && rs.getString("status").equalsIgnoreCase("AVAILABLE")) {
                        // Add guest
                        PreparedStatement psGuest = con.prepareStatement(
                                "INSERT INTO guests VALUES (guest_seq.NEXTVAL,?,?)"
                        );
                        psGuest.setString(1, name);
                        psGuest.setString(2, phone);
                        psGuest.executeUpdate();

                        // Update room status
                        PreparedStatement psUpdate = con.prepareStatement(
                                "UPDATE rooms SET status='BOOKED' WHERE room_id=?"
                        );
                        psUpdate.setInt(1, roomId);
                        psUpdate.executeUpdate();

                        out.println("SUCCESS: Room " + roomId + " booked for " + name);
                    } else {
                        out.println("FAILED: Room not available.");
                    }
                } else {
                    out.println("ERROR: Unknown command.");
                }

            } catch (Exception e) {
                out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}