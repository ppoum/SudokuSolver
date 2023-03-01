package ca.poum.sudokusolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    // Helper class
    private static class Line {
        Cell[] cells;

        public Line(Cell[] cells) {
            this.cells = cells;
        }

        public boolean isValid() {
            List<Integer> wanted = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (Cell c : cells) {
                int value = c.getValue();
                if (value == 0) {
                    return false;
                }
                if (!wanted.contains(value)) {
                    // Value already removed, is duplicate
                    return false;
                }
                // Cast to Integer to signify removing the object w/ value instead of removing object with index
                wanted.remove(Integer.valueOf(value));
            }
            return true;
        }
    }

    private final Cell[][] gameState;

    public Board() {
        gameState = new Cell[9][9];
    }

    public Board(int[][] existingState) {
        gameState = new Cell[9][9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                int i = existingState[y][x];
                gameState[y][x] = (i == 0) ? new Cell() : new Cell(i);
            }
        }
    }

    public Cell[][] getGameState() {
        return gameState;
    }

    public Cell getCell(int x, int y) {
        return gameState[y][x];
    }

    public int getSquare(int x, int y) {
        return (y/3) * 3 + (x/3) % 3;
    }

    public void setCell(int x, int y, int value) {
        gameState[y][x] = new Cell(value);
    }

    public Cell[] getRow(int row) {
        return gameState[row];
    }

    /**
     * Calculates every missing number from a row.
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
                if (cell.getValue() == 0) {
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

    }

    public int solvedCellCount() {
        return (int) Arrays.stream(this.gameState).flatMap(row -> Arrays.stream(row))  // Map 2D array to 1D stream
                .filter(c -> c.getValue() != 0).count();  // Count # of cells that have a value other than 0.
    }

    public boolean isSolved() {
        return this.solvedCellCount() == 81;
    }
}