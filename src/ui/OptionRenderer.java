package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Chang Kon on 26/06/2015.
 */
public class OptionRenderer implements ListCellRenderer<String> {
    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel label = new JLabel(value);
        label.setUI(new VerticalLabelUI(false));
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        label.setBorder(new EmptyBorder(5, 5, 5, 5));

        return label;
    }
}
