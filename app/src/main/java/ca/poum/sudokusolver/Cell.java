package ca.poum.sudokusolver;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cell {
    private int value;
    private List<Integer> pencilMarkings;

    // Constructors
    public Cell() {
        this.value = 0;
        pencilMarkings = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    public Cell(int value) {
        this.value = value;
        pencilMarkings = null;
    }

    // Getters
    public int getValue() {
        return value;
    }

    public @Nullable List<Integer> getPencilMarkings() {
        return pencilMarkings;
    }

    // Setters
    public void setValue(int value) {
        this.value = value;
        this.pencilMarkings = new ArrayList<>();  // Clear markings when value set
    }

    public void setPencilMarkings(List<Integer> pencilMarkings) {
        this.pencilMarkings = pencilMarkings;
    }

    // Converters
    public JButton toJButton(ActionListener l) {
        JButton button = new JButton();

        // Setup button
        button.addActionListener(l);
        button.setLayout(new GridBagLayout());
        button.setFocusable(false);
        button.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;  // Fill area hor. and ver.

        // Setup button label value
        if (this.getValue() != 0) {
            // Cell has value assigned, display big digit
            JLabel label = new JLabel(Integer.toString(this.getValue()), SwingConstants.CENTER);
            label.setForeground(Color.BLACK);
            label.setFont(new Font("Arial", Font.BOLD, 30));
            c.anchor = GridBagConstraints.CENTER;
            button.add(label, c);
        } else {
            // No value assigned, use small digits for pencil markings
            // Anchor array to set each pencil markings in the corners
            int[] anchors = new int[]
                    {GridBagConstraints.FIRST_LINE_START, GridBagConstraints.PAGE_START, GridBagConstraints.FIRST_LINE_END,
                            GridBagConstraints.LINE_START, GridBagConstraints.CENTER, GridBagConstraints.LINE_END,
                            GridBagConstraints.LAST_LINE_END, GridBagConstraints.PAGE_END, GridBagConstraints.LAST_LINE_END};
            c.weightx = 1;
            c.weighty = 1;

            Font pencilFont = new Font("Arial", Font.PLAIN, 12);
            for (int i = 0; i < 9; i++) {
                c.gridx = i % 3;
                c.gridy = i / 3;
                c.anchor = anchors[i];
                String text = (pencilMarkings.contains(i + 1)) ? Integer.toString(i + 1) : " ";
                JLabel label = new JLabel(text, SwingConstants.CENTER);
                label.setForeground(Color.gray);
                label.setFont(pencilFont);
                button.add(label, c);
            }
        }

        return button;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Cell other = (Cell) obj;
        return this.getValue() == other.getValue() && this.getPencilMarkings() == other.getPencilMarkings();
    }
}
