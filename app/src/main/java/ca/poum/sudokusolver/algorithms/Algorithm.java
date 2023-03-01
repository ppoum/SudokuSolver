package ca.poum.sudokusolver.algorithms;

import ca.poum.sudokusolver.Board;

public interface Algorithm {
    void solveCell(Board board);
    void solveIteration(Board board);
}
