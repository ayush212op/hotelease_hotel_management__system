package bookingclient;

import javax.swing.*;
import java.awt.*;

public class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private boolean clicked;
    private int row;
    private ClientDashboard dashboard;

    public ButtonEditor(JCheckBox checkBox, ClientDashboard dashboard) {
        super(checkBox);
        this.dashboard = dashboard;

        button = new JButton();
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> fireEditingStopped());

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateColor();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.row = row;
        clicked = true;
        button.setText((value == null) ? "" : value.toString());
        updateColor();
        return button;
    }

    private void updateColor() {
        String status = dashboard.roomsTable.getValueAt(row, 3).toString();
        if(status.equalsIgnoreCase("AVAILABLE")) {
            button.setBackground(new Color(34, 139, 34)); // Green
            button.setEnabled(true);
        } else {
            button.setBackground(Color.GRAY);
            button.setEnabled(false);
        }
    }

    @Override
    public Object getCellEditorValue() {
        if(clicked) {
            int roomId = (int) dashboard.roomsTable.getValueAt(row, 0);
            dashboard.bookRoom(roomId);
        }
        clicked = false;
        return "Book";
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}