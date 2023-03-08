package ca.poum.sudokusolver.algorithms;

import ca.poum.sudokusolver.Board;

public interface Algorithm {
    boolean solveCell(Board board);
    boolean solveIteration(Board board);
}
