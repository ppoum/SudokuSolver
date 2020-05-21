import java.util.List;

public class BruteforceAlgorithm implements Algorithm {

    /**
     * Checks a cell array and returns the index of the first cell to have a marking that appears once in the array.
     *
     * @param array the cell array to check
     * @return An int array with the index and value of the marking seen once or null if no marking appears less than twice
     */
    private int[] getIndexAppearsOnce(Cell[] array) {
        int[] count = new int[array.length + 1];
        int[] lastSeen = new int[array.length + 1];

        // Loop through ever cell to fill count and lastSeen
        for (int i = 0; i < array.length; i++) {
            Cell c = array[i];
            if (c.getValue() == 0) { // Check if cell has no value
                List<Integer> markings = c.getPencilMarkings();
                for (Integer mark : markings) {
                    count[mark]++;
                    lastSeen[mark] = i;
                }
            }
        }

        for (int i = 1; i <= 9; i++) {
            if (count[i] == 1) {
                // i has only been seen once as a marking, return the lastSeen for that marking
                return new int[]{lastSeen[i], i};
            }
        }
        // Haven't returned, nothing appears once, return null
        return null;
    }

    @Override
    public void solveCell(Board board) {
        if (board.isSolved()) {
            return;
        }

        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = board.getCell(x, y);
                // If only 1 pencil marking, set cell's value to marking
                if (cell.getValue() == 0 && cell.getPencilMarkings().size() == 1) {
                    cell.setValue(cell.getPencilMarkings().get(0));
                    board.calculatePencilMarkings();
                    return;
                }
            }
        }

        // Check every row for pencil marks that appear only once
        for (int i = 0; i < 9; i++) {
            Cell[] row = board.getRow(i);
            int[] answer = getIndexAppearsOnce(row);
            if (answer != null) {
                int pos = answer[0];
                int value = answer[1];
                board.setCell(pos, i, value);
                board.calculatePencilMarkings();
                return;
            }
        }

        // Do the same with every column
        for (int i = 0; i < 9; i++) {
            Cell[] column = board.getColumn(i);
            int[] answer = getIndexAppearsOnce(column);
            if (answer != null) {
                int pos = answer[0];
                int value = answer[1];
                board.setCell(i, pos, value);
                board.calculatePencilMarkings();
                return;
            }
        }

        // Do the same with every subsquare
        for (int i = 0; i < 9; i++) {
            Cell[] square = board.getSquare(i);
            int[] answer = getIndexAppearsOnce(square);
            if (answer != null) {
                int pos = answer[0];
                int value = answer[1];
                int xPos = (3 * (i % 3)) + (pos % 3);
                int yPos = (3 * (i / 3)) + (pos / 3);
                board.setCell(xPos, yPos, value);
                board.calculatePencilMarkings();
                return;
            }
        }
    }
}
