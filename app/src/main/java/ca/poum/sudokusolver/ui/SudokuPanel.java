package ca.poum.sudokusolver.ui;

import ca.poum.sudokusolver.Board;
import ca.poum.sudokusolver.Cell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SudokuPanel extends SquareJPanel {
    private Board board;
    private final JButton[][] gridButtons = new JButton[9][9];
    private boolean inSetupMode = true;
    private Integer activeX, activeY;

    public SudokuPanel(Board board) {
        setLayout(new GridLayout(9, 9));
        this.board = board;
        populatePanelGrid();
    }

    private void populatePanelGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int finalRow = row;
                int finalCol = col;
                JButton button = board.getCell(col, row).toJButton(e -> setActiveCell(finalCol, finalRow));
                button.setBorder(createSudokuCellBorder(col, row, Color.BLACK));
                gridButtons[row][col] = button;
                this.add(button);
            }
        }
    }

    public void updateGridValues() {
        this.removeAll();  // Remove existing grid components
        populatePanelGrid();  // Re-add grid components using new values
        this.validate();  // Refresh panel
    }

    public void exitSetupMode() {
        this.inSetupMode = false;

        // De-select currently active button
        if (activeX != null && activeY != null) {
            Border border = createSudokuCellBorder(activeX, activeY, Color.BLACK);
            gridButtons[activeY][activeX].setBorder(border);
            gridButtons[activeY][activeX].setBackground(Color.white);
        }
    }

    private void setActiveCell(int x, int y) {
        if (!inSetupMode) return;

        if (activeX != null && activeY != null) {
            Border border = createSudokuCellBorder(activeX, activeY, Color.BLACK);
            gridButtons[activeY][activeX].setBorder(border);
            gridButtons[activeY][activeX].setBackground(Color.WHITE);
        }

        Border border = createSudokuCellBorder(x, y, Color.BLUE);
        gridButtons[y][x].setBorder(border);
        gridButtons[y][x].setBackground(Color.LIGHT_GRAY);
        activeX = x;
        activeY = y;
    }

    public void moveActiveCell(String direction) {
        if (activeX == null || activeY == null) {
            // No active cell, moving impossible
            return;
        }

        switch (direction) {
            case "UP" -> {
                // Only move if not already at top
                if (activeY > 0) {
                    setActiveCell(activeX, activeY - 1);
                }
            }
            case "DOWN" -> {
                // Only move if not already at bottom
                if (activeY < 8) {
                    setActiveCell(activeX, activeY + 1);
                }
            }
            case "LEFT" -> {
                // Only move if not already at leftmost pos
                if (activeX > 0) {
                    setActiveCell(activeX - 1, activeY);
                }
            }
            case "RIGHT" -> {
                // Only move if not already at rightmost pos
                if (activeX < 8) {
                    setActiveCell(activeX + 1, activeY);
                }
            }
        }
    }

    public void writeToActiveCell(String s) {
        if (activeX == null || activeY == null) return;  // Can't write if no active cell

        // Clear cell and generate pencil markings
        Cell c = this.board.getCell(activeX, activeY);
        int previousVal = c.getValue();
        board.setCell(activeX, activeY, 0);
        board.calculateCellPencilMarkings(activeX, activeY);
        c = this.board.getCell(activeX, activeY);

        if (s.equals("-1")) {
            // Clear cell
            return;
        }

        // Writing new value, check if actually valid
        int newValue = Integer.parseInt(s);

        if (c.getPencilMarkings().contains(newValue)) {
            board.setCell(activeX, activeY, newValue);
        } else {
            // Invalid value entered, change color to red & reset cell value to value before change
            board.setCell(activeX, activeY, previousVal);
            Border border = createSudokuCellBorder(activeX, activeY, Color.RED);
            gridButtons[activeY][activeX].setBorder(border);
            return;
        }


        this.board.calculatePencilMarkings();
        this.updateGridValues();

        // updateGridValues clears active cell border, reset it as active
        this.setActiveCell(activeX, activeY);
    }

    public int[][] toIntMatrix() {
        int[][] val = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Component component = gridButtons[row][col].getComponent(0);
                if (gridButtons[row][col].getComponents().length == 1) {
                    // 1 component means big number, value defined
                    JLabel label = (JLabel) component;
                    val[row][col] = Integer.parseInt(label.toString());
                } else {
                    // More than 1 component, no value defined
                    val[row][col] = 0;
                }
            }
        }
        return val;
    }

    /**
     * Creates a border based on the position of the cell
     *
     * @param x     column coordinate of the cell
     * @param y     row coordinate of the cell
     * @param color Color wanted for the border
     * @return Returns a border with some sides thicker than the others to represent edge or box delimitation.
     */
    private Border createSudokuCellBorder(int x, int y, Color color) {
        int top = (y == 0 || y == 3 || y == 6) ? 2 : 1;
        int left = (x == 0 || x == 3 || x == 6) ? 2 : 1;
        int bottom = (y == 2 || y == 5 || y == 8) ? 2 : 1;
        int right = (x == 2 || x == 5 || x == 8) ? 2 : 1;
        return new MatteBorder(top, left, bottom, right, color);
    }
}
