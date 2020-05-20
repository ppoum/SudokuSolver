import javax.swing.*;
import java.awt.*;
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

    public List<Integer> getPencilMarkings() {
        return pencilMarkings;
    }

    // Setters
    public void setValue(int value) {
        this.value = value;
    }

    public void setPencilMarkings(List<Integer> pencilMarkings) {
        this.pencilMarkings = pencilMarkings;
    }

    public void addPencilMarking(int number) {
        // Only try to add if it isn't already in the markings
        if (pencilMarkings.contains(number)) {
            return;
        }
        // When adding a pencil marking, we need to keep the markings ordered
        int index = 0;
        while (pencilMarkings.get(index) < number) {
            index++;
        }
        pencilMarkings.add(index, number);
    }

    public void removePencilMarking(int i) {
        pencilMarkings.remove(i);
    }

    // Converters
    public JPanel toJPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.white);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        if (this.getValue() != 0) {
            // Has set value, use big number
            JLabel l = new JLabel(Integer.toString(this.getValue()), SwingConstants.CENTER);
            l.setForeground(Color.black);
            l.setFont(new Font("Arial", Font.BOLD, 30));
            c.anchor = GridBagConstraints.CENTER;
            panel.add(l, c);
        } else {
            // No set value, use pencil markings

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
                JLabel l = new JLabel(text, SwingConstants.CENTER);
                l.setForeground(Color.gray);
                l.setFont(pencilFont);
                panel.add(l, c);
            }
        }
        return panel;
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
