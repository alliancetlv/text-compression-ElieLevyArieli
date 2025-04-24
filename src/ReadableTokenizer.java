/**
 * A tokenizer for encoding and decoding LZ77-style compression tokens in a readable format.
 * Tokens are represented as: "^distance,length^".
 */
public class ReadableTokenizer implements Tokenizer {

    //TODO: TASK 4
    public String toTokenString(int distance, int length) {
            return "^" + distance + "," + length + "^";
    }

    // TODO TASK 4
    public int[] fromTokenString(String input, int startIndex) {
        int endIndex = input.indexOf('^', startIndex + 1);
        if (endIndex == -1) {
            return new int[0];
        }
        int commaIndex = input.indexOf(',', startIndex);
        if (commaIndex == -1 || commaIndex > endIndex) {
            return new int[0];
        }
        String distanceStr = input.substring(startIndex + 1, commaIndex).trim();
        String lengthStr = input.substring(commaIndex + 1, endIndex).trim();
        if (!isNumeric(distanceStr) || !isNumeric(lengthStr)) {
            return new int[0];
        }
        int distance = Integer.parseInt(distanceStr);
        int length = Integer.parseInt(lengthStr);
        int tokenLength = endIndex - startIndex + 1;

        return new int[] { distance, length, tokenLength };
    }


    private static boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}