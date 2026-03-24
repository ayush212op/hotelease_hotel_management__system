package bookingclient;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
        setFocusPainted(false);
        setBorderPainted(false);
        setBackground(new Color(100, 149, 237)); // Cornflower blue
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText((value == null) ? "" : value.toString());

        // Change color based on availability
        String status = table.getValueAt(row, 3).toString();
        if(status.equalsIgnoreCase("AVAILABLE")) {
            setBackground(new Color(34, 139, 34)); // Green
        } else {
            setBackground(Color.GRAY); // Disabled
        }

        // Hover effect when selected
        if (isSelected) {
            setBackground(getBackground().darker());
        }

        return this;
    }
}