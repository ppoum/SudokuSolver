public class BruteforceAlgorithm implements Algorithm {

    @Override
    public void solveStep(Board board) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Cell cell = board.getCell(x, y);
                if (cell.getValue() == 0 && cell.getPencilMarkings().size() == 1) {
                    cell.setValue(cell.getPencilMarkings().get(0));
                }
            }
        }

        board.calculatePencilMarkings();
    }
}
