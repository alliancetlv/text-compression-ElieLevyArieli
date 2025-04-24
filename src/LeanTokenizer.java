/**
 * A lightweight tokenizer for encoding and decoding.
 * Uses a compact three-character format to represent references in the compressed text.
 */
public class LeanTokenizer implements Tokenizer {
    public LeanTokenizer() {

    }

    //TODO: TASK 10
    public int[] fromTokenString(String tokenText, int index) {
        int[] result = new int[3];
        char distanceChar = tokenText.charAt(index + 1);
        char lengthChar = tokenText.charAt(index + 2);
        result[0] = (int) distanceChar;
        result[1] = (int) lengthChar;
        result[2] = 3;
        return result;
    }
    public String toTokenString(int distance, int length) {
        char distanceChar = (char) distance;
        char lengthChar = (char) length;
        return "^" + distanceChar + lengthChar + "^";
    }
}