import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Base64;

public class SudokuFrame extends JFrame {
    private static class SquareJPanel extends JPanel {
        /**
         * Overriding #getPreferredSize and #getMinimumSize to force JPanel to be square
         */
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

    /**
     * Custom JPanel class representing the sudoku grid with changeable cells
     */
    private class SetupPanel extends SquareJPanel {
        private final JButton[][] buttons = new JButton[9][9];
        private Integer activeX = null;
        private Integer activeY = null;


        /**
         * Fills a 9x9 2D array of JButton created to look like a sudoku grid (w/ custom border, background, etc)
         * @param buttonArray The array to fill
         * @param presetGrid Int array representing preset cell numbers (0 means empty cell, null array means empty grid)
         */
        private void createButtonArray(JButton[][] buttonArray, int[] presetGrid) {
            if (presetGrid == null) {
                presetGrid = new int[81]; // default value for int array is 0
            }

            // Fill buttonArray with buttons
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    JButton button;
                    // Check presetGrid to see if cell should be empty or have a preset number
                    int oneDimensionalIndex = 9 * y + x;
                    int presetNumber = presetGrid[oneDimensionalIndex];
                    if (presetNumber == 0) {
                        button = new JButton(" ");
                    } else {
                        button = new JButton(Integer.toString(presetNumber));
                    }

                    button.setBorder(createSudokuCellBorder(x, y, Color.black));
                    button.setBackground(Color.white);

                    // Set focusable to false so the spacebar can't simulate a click on the button
                    button.setFocusable(false);
                    button.setFont(new Font("Arial", Font.BOLD, 30));
                    // Create finalX and finalY to be able to use them in the lambda function
                    int finalX = x;
                    int finalY = y;
                    button.addActionListener(e -> setActiveCell(finalX, finalY));

                    this.add(button);
                    buttonArray[y][x] = button;
                }
            }
        }

        private void setActiveCell(int x, int y) {
            if (activeX != null && activeY != null) {
                // If a button was previously active, reset it to normal border and background
                Border border = createSudokuCellBorder(activeX, activeY, Color.black);
                buttons[activeY][activeX].setBorder(border);
                buttons[activeY][activeX].setBackground(Color.white);
            }
            // Change active button's border and background to highlight it in UI
            Border border = createSudokuCellBorder(x, y, Color.blue);
            buttons[y][x].setBorder(border);
            buttons[y][x].setBackground(Color.lightGray);
            activeX = x;
            activeY = y;
        }

        public void moveActiveCell(String direction) {
            if (activeX == null || activeY == null) {
                return;
            }

            switch (direction) {
                case "UP": {
                    // Only move if not already at top
                    if (activeY > 0) {
                        setActiveCell(activeX, activeY - 1);
                    }
                    break;
                }
                case "DOWN": {
                    // Only move if not already at bottom
                    if (activeY < 8) {
                        setActiveCell(activeX, activeY + 1);
                    }
                    break;
                }
                case "LEFT": {
                    // Only move if not already at leftmost pos
                    if (activeX > 0) {
                        setActiveCell(activeX - 1, activeY);
                    }
                    break;
                }
                case "RIGHT": {
                    // Only move if not already at rightmost pos
                    if (activeX < 8) {
                        setActiveCell(activeX + 1, activeY);
                    }
                }
            }
        }

        public void changeNumberInActiveCell(String s) {
            if (activeX != null && activeY != null) {
                if (s.equals("-1")) {
                    buttons[activeY][activeX].setText(" ");
                } else {
                    buttons[activeY][activeX].setText(s);
                }
            }
        }

        public SetupPanel(int[] presetGrid) {
            setLayout(new GridLayout(9, 9));
            // Fill the buttons array with buttons
            createButtonArray(buttons, presetGrid);

        }

        public int[][] getButtonsAsIntArray() {
            int[][] intButtons = new int[9][9];
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    String text = buttons[y][x].getText();
                    if (text.equals(" ")) {
                        intButtons[y][x] = 0;
                    } else {
                        intButtons[y][x] = Integer.parseInt(text);
                    }
                }
            }
            return intButtons;
        }
    }

    private class SolvingPanel extends SquareJPanel {
        Board board;
        JPanel[][] cellArray = new JPanel[9][9];

        private void fillCellArray(JPanel[][] cellArray) {
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    JPanel panel = board.getCell(x, y).toJPanel();
                    panel.setBorder(createSudokuCellBorder(x, y, Color.black));
                    cellArray[y][x] = panel;
                    this.add(panel);
                }
            }
        }

        public SolvingPanel(Board board) {
            setLayout(new GridLayout(9, 9));
            this.board = board;
            fillCellArray(cellArray);
        }

        public void updateValues() {
            this.removeAll();
            fillCellArray(cellArray);
            this.validate();
        }
    }

    // Helper functions
    /**
     * Creates a border based on the position of the cell
     * @param x x coordinate of the cell
     * @param y y coordinate of the cell
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


    // --
    // --- SETUP SECTION ---
    // --
    private boolean settingUp = true;
    SetupPanel setupPanel;

    // Key binding processing function
    private class KeyBindingAction extends AbstractAction {
        private final String s;
        KeyBindingAction(String s) {
            this.s = s;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (settingUp) {
                if (s.equals("UP") || s.equals("DOWN") || s.equals("LEFT") || s.equals("RIGHT")) {
                    setupPanel.moveActiveCell(s);
                    return;
                }
                setupPanel.changeNumberInActiveCell(s);
            }
        }
    }

    private void registerKeyBindings() {
        // Register digits 1 to 9 to an AbstractAction that links to the SetupPanel
        for (int i = 1; i <= 9; i++) {
            this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(Integer.toString(i)), Integer.toString(i));
            this.getRootPane().getActionMap().put(Integer.toString(i), new KeyBindingAction(Integer.toString(i)));
        }
        // Register a new Action for the spacebar to clear the cell
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("SPACE"), " ");
        this.getRootPane().getActionMap().put(" ", new KeyBindingAction("-1"));
        // Register actions for the arrow keys to move the cell
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("UP"), "UP");
        this.getRootPane().getActionMap().put("UP", new KeyBindingAction("UP"));
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("DOWN"), "DOWN");
        this.getRootPane().getActionMap().put("DOWN", new KeyBindingAction("DOWN"));
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("LEFT"), "LEFT");
        this.getRootPane().getActionMap().put("LEFT", new KeyBindingAction("LEFT"));
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("RIGHT"), "RIGHT");
        this.getRootPane().getActionMap().put("RIGHT", new KeyBindingAction("RIGHT"));

    }

    private void setGeneralFrameSettings() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit program when window is closed
        setSize(500, 550); // Set size of 500x550px
        setLayout(new GridBagLayout()); // Use a GridBagLayout (necessary so the SetupPanel stays square)
    }

    private JPanel createSetupButtonPanel() {
        JPanel buttonPanel = new JPanel();

        // finishButton is the button to exit the setup mode and enter the solving mode
        JButton finishButton = new JButton("Finish");
        finishButton.setFocusable(false);
        finishButton.addActionListener(e -> exitSetup());
        buttonPanel.add(finishButton);

        // exportButton prints the current board as a b64 string to the console
        // TODO Create a popup with a text zone with the b64 string instead of printing to console.
        JButton exportButton = new JButton("Export");
        exportButton.setFocusable(false);
        exportButton.addActionListener(e -> {
            int[][] numbers = setupPanel.getButtonsAsIntArray();
            StringBuilder sb = new StringBuilder();
            for (int[] row : numbers) {
                for (int i : row) {
                    sb.append(i);
                }
            }
            String encoded = new String(Base64.getEncoder().encode(sb.toString().getBytes()));
            System.out.println(encoded);
        });
        buttonPanel.add(exportButton);

        return buttonPanel;
    }

    private void exitSetup() {
        settingUp = false;
        board = new Board(setupPanel.getButtonsAsIntArray());

        // Remove setup components
        this.getContentPane().removeAll();
        startSolving();
    }

    // Solving vars
    Board board;
    SolvingPanel solvingPanel;
    Algorithm algorithm;

    private void startSolving() {
        algorithm = new BruteforceAlgorithm(); // Config to change algorithm type? Algorithm factory?
        board.calculatePencilMarkings();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        // Add solving panel
        solvingPanel = new SolvingPanel(board);
        this.add(solvingPanel, constraints);

        // Add button panel
        JPanel buttonPanel = new JPanel();

        JButton nextStepButton = new JButton("Next step");
        nextStepButton.addActionListener(e -> nextStepAction());
        buttonPanel.add(nextStepButton);

        constraints.gridx++;
        this.add(buttonPanel, constraints);

        validate();
        this.getContentPane().repaint();
    }

    // Called by nextStepButton when clicked
    private void nextStepAction() {
        algorithm.solveStep(board);
        solvingPanel.updateValues();
    }

    // Constructor
    public SudokuFrame() {
        this(null);
    }

    public SudokuFrame(int[] presetGrid) {
        registerKeyBindings();

        setGeneralFrameSettings();

        setupPanel = new SetupPanel(presetGrid);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        this.add(setupPanel, constraints);

        // buttonPanel contains the finish button and the export button
        JPanel buttonPanel = createSetupButtonPanel();
        constraints.gridy = 1; // Change gridy so the panel is located under the setupPanel
        this.add(buttonPanel, constraints);

        setVisible(true);
    }

}
