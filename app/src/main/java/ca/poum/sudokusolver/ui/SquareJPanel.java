package ca.poum.sudokusolver.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Simple wrapper class around JPanel overwriting #getPreferredSize and #getMinimumSize
 * to force a SquareJPanel to have a square size.
 */
public class SquareJPanel extends JPanel {
    @Override
    public Dimension getPreferredSize() {
        Dimension d = this.getParent().getSize();
        int newSize = Math.min(d.width, d.height);
        newSize = (newSize == 0) ? 100: newSize;
        return new Dimension(newSize, newSize);
    }
    @Override
    public Dimension getMinimumSize() {
        Dimension d = this.getParent().getSize();
        int newSize = Math.min(d.width, d.height);
        newSize = (newSize == 0) ? 100: newSize - 50;
        return new Dimension(newSize, newSize);
    }
}
