package ca.poum.sudokusolver;

import ca.poum.sudokusolver.algorithms.Algorithm;
import ca.poum.sudokusolver.algorithms.BruteforceAlgorithm;
import ca.poum.sudokusolver.ui.B64Popup;
import ca.poum.sudokusolver.ui.SudokuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SudokuFrame extends JFrame {

    // Key binding processing function
    private class KeyBindingAction extends AbstractAction {
        private final String s;

        KeyBindingAction(String s) {
            this.s = s;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (s.equals("UP") || s.equals("DOWN") || s.equals("LEFT") || s.equals("RIGHT")) {
                sudokuPanel.moveActiveCell(s);
                return;
            }
            sudokuPanel.writeToActiveCell(s);

        }
    }

    Board board;
    SudokuPanel sudokuPanel;
    Algorithm algorithm;

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

    private void exitSetup() {
        sudokuPanel.exitSetupMode();

        // Remove setup view, create solving view
        this.getContentPane().removeAll();
        enterSolvingMode();
    }

    private void enterSolvingMode() {
        // Set window title
        this.setTitle(String.format("Solving board: %d/81", board.solvedCellCount()));

        algorithm = new BruteforceAlgorithm(); // Config to change algorithm type? Algorithm factory?
        board.calculatePencilMarkings();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;

        // Re-add sudoku panel (removed when exiting setup view)
        this.add(sudokuPanel, constraints);

        // Add button panel
        JPanel buttonPanel = new JPanel();

        JButton nextStepButton = new JButton("Solve next cell");
        nextStepButton.addActionListener(e -> nextStepAction());
        buttonPanel.add(nextStepButton);

        constraints.gridx++;
        this.add(buttonPanel, constraints);

        validate();
        this.getContentPane().repaint();
    }

    // Called by nextStepButton when clicked
    private void nextStepAction() {
        algorithm.solveIteration(board);
        sudokuPanel.updateGridValues();
        // Update window title
        this.setTitle(String.format("Solving board: %d/81", board.solvedCellCount()));
    }

    public void createSetupView() {
        // Register keybindings to move select cell when creating board
        registerKeyBindings();

        // Set global settings
        UIManager.put("Button.select", Color.TRANSLUCENT);
        setLocationRelativeTo(null);  // Open window in center of screen
        this.setTitle("Setting up the board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit program when window is closed
        setSize(500, 550); // Set size of 500x550px
        setLayout(new GridBagLayout()); // Use a GridBagLayout (necessary so the SudokuPanel stays square)

        // Set content for view
        sudokuPanel = new SudokuPanel(board);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        this.add(sudokuPanel, constraints);

        // Create additional panel containing control buttons
        JPanel buttonPanel = createSetupButtonsPanel();
        constraints.gridy = 1; // Change gridy so the panel is located under the setupPanel
        this.add(buttonPanel, constraints);

        // Once finished, show windows
        setVisible(true);
    }

    /**
     * Creates a panel containing the "Finish" and "Export" buttons. The finish button
     * lets the program enter the solving stage, and export calculates the b64 representation
     * of the current grid.
     *
     * @return The panel containing both buttons.
     */
    private JPanel createSetupButtonsPanel() {
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
        exportButton.addActionListener(e -> new B64Popup(this, sudokuPanel));
        buttonPanel.add(exportButton);

        return buttonPanel;
    }

    // Constructor
    public SudokuFrame() {
        this(null);
    }

    public SudokuFrame(int[][] presetGrid) {
        board = new Board(presetGrid);
        createSetupView();
    }

}
