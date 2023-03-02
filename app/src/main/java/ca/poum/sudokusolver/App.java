package ca.poum.sudokusolver;

import java.util.Base64;

public class App {

    public static void main(String[] args) {
        if (args.length == 1) {
            // Decode args[0]
            String encoded = args[0];
            Base64.Decoder decoder = Base64.getDecoder();
            String decoded = new String(decoder.decode(encoded));
            int[][] decodedIntArray = new int[9][9];

            for (int i = 0; i < 81; i++) {
                int col = i % 9;
                int row = i / 9;
                decodedIntArray[col][row] = Character.getNumericValue(decoded.charAt(i));
            }

            // Create new SudokuFrame with the decodedIntArray;
            new SudokuFrame(decodedIntArray);
        } else {
            // Create empty SudokuFrame
            new SudokuFrame();
        }
    }
}
