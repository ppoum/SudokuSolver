import java.util.Base64;

public class SudokuSolver {

    public static void main(String[] args) {
        if (args.length == 1) {
            // Decode args[0]
            String encoded = args[0];
            Base64.Decoder decoder = Base64.getDecoder();
            String decoded = new String(decoder.decode(encoded));
            int[] decodedIntArray = new int[decoded.length()];
            for (int i = 0; i < decodedIntArray.length; i++) {
                decodedIntArray[i] = Character.getNumericValue(decoded.charAt(i));
            }
            // Create new SudokuFrame with the decodedIntArray;
            new SudokuFrame(decodedIntArray);
        } else {
            // Create empty SudokuFrame
            new SudokuFrame();
        }

    }
}
