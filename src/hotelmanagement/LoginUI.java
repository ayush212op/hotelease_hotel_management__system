package hotelmanagement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

public class LoginUI extends JFrame {

    private JLabel imgLabel;
    private Image originalImage;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginUI(){

        setTitle("HotelEase Login");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadImage();
        buildRightPanel();

        resizeImage();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeImage();
            }
        });

        setVisible(true);
    }

    private void loadImage(){

        ImageIcon img = new ImageIcon(
                "resources/Hotelease_Edited.png"
        );

        originalImage = img.getImage();

        imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(JLabel.CENTER);

        add(imgLabel, BorderLayout.WEST);
    }

    private void buildRightPanel(){

        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Dancing Script", Font.PLAIN, 50));
        title.setForeground(new Color(189,147,249));
        title.setHorizontalAlignment(JLabel.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(title, gbc);

        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Username");
        gbc.gridx = 0;
        gbc.gridy = 1;
        rightPanel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        rightPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password");
        gbc.gridx = 0;
        gbc.gridy = 2;
        rightPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        rightPanel.add(loginButton, gbc);

        JButton registerButton = new JButton("Create Account");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        rightPanel.add(registerButton, gbc);

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> registerUser());
        passwordField.addActionListener(e -> loginUser());

        add(rightPanel, BorderLayout.CENTER);
    }

    private Connection getConnection(){

        try{

            Class.forName("oracle.jdbc.driver.OracleDriver");

            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe",
                    "DB_USER", "DB_PASS"
            );

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Database connection failed:\n"+e.getMessage());

            return null;
        }
    }

    private void loginUser(){

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Please enter username and password");
            return;
        }

        try{

            Connection con = getConnection();
            if(con == null) return;

            String query = "SELECT * FROM users WHERE username=? AND password=?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1,username);
            ps.setString(2,password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                JOptionPane.showMessageDialog(this,"Login Successful");

                dispose(); // close login window

                new DashboardUI(); // open main system
            }
            else{

                JOptionPane.showMessageDialog(this,"Invalid credentials");
            }

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Login error:\n"+e.getMessage());
        }
    }

    private void registerUser(){

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this,"Enter username and password");
            return;
        }

        try{

            Connection con = getConnection();
            if(con == null) return;

            String query = "INSERT INTO users VALUES (user_seq.NEXTVAL,?,?)";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1,username);
            ps.setString(2,password);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Account created");

            con.close();

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Registration error:\n"+e.getMessage());
        }
    }

    private void resizeImage(){

        int frameHeight = getHeight();

        Image scaledImage = originalImage.getScaledInstance(
                -1,
                frameHeight,
                Image.SCALE_SMOOTH
        );

        imgLabel.setIcon(new ImageIcon(scaledImage));
    }

    public static void main(String[] args) {

        try{
            FlatDraculaIJTheme.setup();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(LoginUI::new);
    }
}