package ca.poum.sudokusolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private final Cell[][] gameState;

    public Board() {
        gameState = new Cell[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                gameState[y][x] = new Cell();
            }
        }
    }

    public Board(int[][] existingState) {
        gameState = new Cell[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int i = existingState[y][x];
                gameState[y][x] = (i == 0) ? new Cell() : new Cell(i);
            }
        }

        // Validate board
        for (int row = 0; row < 9; row++) {
            List<Integer> seen = new ArrayList<>();
            for (int col = 0; col < 9; col++) {
                int v = this.getCell(col, row).getValue();
                if (v != 0 && seen.contains(v)) {
                    throw new IllegalArgumentException("Invalid board state");
                }
                seen.add(v);
            }
        }

        for (int col = 0; col < 9; col++) {
            List<Integer> seen = new ArrayList<>();
            for (int row = 0; row < 9; row++) {
                int v = this.getCell(col, row).getValue();
                if (v != 0 && seen.contains(v)) {
                    throw new IllegalArgumentException("Invalid board state");
                }
                seen.add(v);
            }
        }

        for (int square = 0; square < 9; square++) {
            List<Integer> seen = new ArrayList<>();
            for (Cell c : this.getSquare(square)) {
                int v = c.getValue();
                if (v != 0 && seen.contains(v)) {
                    throw new IllegalArgumentException("Invalid board state");
                }
                seen.add(v);
            }
        }

        // If no exception thrown, valid board, calculate markings
        this.calculatePencilMarkings();
    }

    public Cell getCell(int x, int y) {
        return gameState[y][x];
    }

    public int getSquare(int x, int y) {
        return (y / 3) * 3 + (x / 3) % 3;
    }

    public void setCell(int x, int y, int value) {
        gameState[y][x] = new Cell(value);
        this.calculateCellPencilMarkings(x, y);
    }

    public Cell[] getRow(int row) {
        return gameState[row];
    }

    /**
     * Calculates every missing number from a row.
     *
     * @param row The index of the row to use.
     * @return An array of cells constituting every missing number.
     */
    public Cell[] getMissingFromRow(int row) {
        List<Integer> missing = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Cell c : gameState[row]) {
            int value = c.getValue();
            if (value != 0) {
                missing.remove((Integer) value);
            }
        }
        Cell[] missingArray = new Cell[missing.size()];
        for (int i = 0; i < missing.size(); i++) {
            missingArray[i] = new Cell(missing.get(i));
        }
        return missingArray;
    }

    public Cell[] getColumn(int column) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            cells.add(gameState[i][column]);
        }
        return cells.toArray(new Cell[0]);
    }

    public Cell[] getMissingFromColumn(int column) {
        List<Integer> missing = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (int i = 0; i < 9; i++) {
            int value = gameState[i][column].getValue();
            if (value != 0) {
                missing.remove((Integer) value);
            }
        }
        Cell[] missingArray = new Cell[missing.size()];
        for (int i = 0; i < missing.size(); i++) {
            missingArray[i] = new Cell(missing.get(i));
        }
        return missingArray;
    }

    public Cell[] getSquare(int square) {
        int xOffset = 3 * (square % 3);
        int yOffset = 3 * (square / 3);

        List<Cell> cells = new ArrayList<>();
        for (int y = 0; y < 3; y++) {
            cells.addAll(Arrays.asList(gameState[y + yOffset]).subList(xOffset, 3 + xOffset));
        }
        return cells.toArray(new Cell[0]);
    }

    public Cell[] getMissingFromSquare(int square) {
        List<Integer> missing = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Cell c : getSquare(square)) {
            int value = c.getValue();
            if (value != 0) {
                missing.remove((Integer) value);
            }
        }
        Cell[] missingArray = new Cell[missing.size()];
        for (int i = 0; i < missing.size(); i++) {
            missingArray[i] = new Cell(missing.get(i));
        }
        return missingArray;
    }

    private boolean twoWayContains(Cell cell, List<Cell> listA, List<Cell> listB) {
        return (listA.contains(cell) && listB.contains(cell));
    }

    public void calculateCellPencilMarkings(int x, int y) {
        Cell cell = getCell(x, y);
        if (cell.getValue() != 0) {
            // No markings for cells with values
            return;
        }

        Cell[] missingRow = getMissingFromRow(y);
        List<Cell> missingCol = Arrays.asList(getMissingFromColumn(x));
        List<Cell> missingSquare = Arrays.asList(getMissingFromSquare(getSquare(x, y)));

        List<Integer> markings = new ArrayList<>();
        for (Cell c : missingRow) {
            if (twoWayContains(c, missingCol, missingSquare)) {
                // Cell value missing for row, col and square, add to pencil markings
                markings.add(c.getValue());
            }
        }
        cell.setPencilMarkings(markings);
    }

    public void calculatePencilMarkings() {
        List<List<Cell>> missingRows = new ArrayList<>();
        List<List<Cell>> missingColumns = new ArrayList<>();

        for (int row = 0; row < 9; row++) {
            missingRows.add(Arrays.asList(getMissingFromRow(row)));
        }

        for (int column = 0; column < 9; column++) {
            missingColumns.add(Arrays.asList(getMissingFromColumn(column)));
        }

        // Iterate over every cell
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = getCell(x, y);
                if (cell.getValue() != 0) continue;
                // Empty cell, recalculate pencil markings
                List<Cell> missingRow = missingRows.get(y);
                List<Cell> missingColumn = missingColumns.get(x);
                List<Cell> missingSquare = Arrays.asList(getMissingFromSquare(getSquare(x, y)));
                List<Integer> markings = new ArrayList<>();
                for (Cell c : missingRow) {
                    // If c (which is already missing from row) is also missing from col and square,
                    // cell could have that value, add to markings
                    if (twoWayContains(c, missingColumn, missingSquare)) {
                        markings.add(c.getValue());
                    }
                }
                cell.setPencilMarkings(markings);

            }
        }

    }

    public int solvedCellCount() {
        return (int) Arrays.stream(this.gameState).flatMap(Arrays::stream)  // Map 2D array to 1D stream
                .filter(c -> c.getValue() != 0).count();  // Count # of cells that have a value other than 0.
    }

    public boolean isSolved() {
        return this.solvedCellCount() == 81;
    }
}