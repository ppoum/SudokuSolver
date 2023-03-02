package ca.poum.sudokusolver.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Base64;

public class B64Popup {
    static class FocusTextField extends JTextField {
        public FocusTextField(String text) {
            super(text);
            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    FocusTextField.this.select(0, getText().length());
                }

                @Override
                public void focusLost(FocusEvent e) {
                    FocusTextField.this.select(0, 0);
                }
            });
        }
    }


    public B64Popup(Component parent, SudokuPanel panel) {
        final Object[] buttonsText = {"Ok", "Copy"};

        int[][] gridVal = panel.toIntMatrix();
        StringBuilder sb = new StringBuilder();
        for (int[] row : gridVal) {
            for (int i : row) {
                sb.append(i);
            }
        }
        String b64 = new String(Base64.getEncoder().encode(sb.toString().getBytes()));

        int res = JOptionPane.showOptionDialog(parent, this.createPanel(b64), "Export the Sudoku grid",
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonsText, null);

        if (res == JOptionPane.NO_OPTION) {
            // User clicked copy button
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(b64), null);
        }
    }

    private JPanel createPanel(String b64) {
        JPanel panel = new JPanel();
        FocusTextField textField = new FocusTextField(b64);
        textField.setEditable(false);
        panel.add(textField);
        return panel;
    }

}
