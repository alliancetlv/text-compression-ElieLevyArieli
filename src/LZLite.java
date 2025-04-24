/**
 * Class for performing LZ77 compression/decompression.
 */


/**
 * Class for performing compression/decompression loosely based on LZ77.
 */
public class LZLite {
    public static int MAX_WINDOW_SIZE = 65535;
    private int windowSize;
    private String slidingWindow;
    private Tokenizer tokenizer;

    public LZLite(int windowSize, boolean readable) {
        this.windowSize = windowSize;
        this.slidingWindow = "";
        tokenizer = new ReadableTokenizer();
    }

    // Append string to sliding window and ensure its size does not exceed the window size
    public void appendToSlidingWindow(String st) {
        slidingWindow += st;
        int diff = slidingWindow.length() - windowSize;
        if (diff > 0) {
            slidingWindow = slidingWindow.substring(diff);
        }
    }


    public String maxMatchInWindow(String input, int pos) {
        String maxSeq = "";
        String seq = "";
        for (int i = 0; i < slidingWindow.length(); i++) {
            for (int j = 0; j < slidingWindow.length() - i && j < input.length() - pos; j++) {
                if (slidingWindow.charAt(i + j) == input.charAt(pos + j)) {
                    seq += slidingWindow.charAt(i + j);
                } else {
                    if (seq.length() > maxSeq.length()) {
                        maxSeq = seq;
                    }
                    seq = "";
                    break;
                }
            }
        }
        return maxSeq;
    }


    public String zip(String input) {
        StringBuilder compressed = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            int bestOffset = 0;
            int bestLength = 0;
            int startWindow = Math.max(0, i - windowSize);
            for (int j = startWindow; j < i; j++) {
                int k = 0;
                while (i + k < input.length() && input.charAt(j + k) == input.charAt(i + k)) {
                    k++;
                }
                if (k > bestLength) {
                    bestLength = k;
                    bestOffset = i - j;
                }
            }
            if (bestLength >= 3) {
                String token = "^" + bestOffset + "," + bestLength + "^";
                // Only create a token if it's more efficient than storing the literal
                if (token.length() < bestLength + 2) {
                    compressed.append(token);
                    i += bestLength;
                    continue;
                }
            }
            compressed.append(input.charAt(i));
            i++;
        }
        return compressed.toString();
    }


    public static String zipFileName(String fileName) {
        if (!fileName.endsWith(".txt")) return null;
        return fileName.substring(0, fileName.length() - 4) + ".lz77.txt";
    }


    public static String unzipFileName(String fileName) {
        return fileName.substring(0, fileName.length() - 9);
    }


    public static String zipFile(String file, int windowSize, boolean readable) {
        String newName = zipFileName(file);
        if (newName == null)
            return null;

        String content = FileUtils.readFile(file);
        LZLite lz = new LZLite(windowSize, readable);
        String compressed = lz.zip(content);
        FileUtils.writeFile(newName, compressed);

        return newName;
    }


    public String unzip(String input) {
        StringBuilder expandedText = new StringBuilder();
        int pos = 0;
        while (pos < input.length()) {
            if (input.charAt(pos) == '^') {
                if (pos + 2 >= input.length()) {
                    expandedText.append('^');
                    pos++;
                    continue;
                }
                int[] tokenData = tokenizer.fromTokenString(input, pos);
                if (tokenData.length != 3) {
                    expandedText.append('^');
                    pos++;
                    continue;
                }
                int distance = tokenData[0];
                int length = tokenData[1];
                int tokenLength = tokenData[2];
                int start = expandedText.length() - distance;
                int end = start + length;
                if (start >= 0 && end <= expandedText.length()) {
                    String referencedText = expandedText.substring(start, end);
                    expandedText.append(referencedText);
                    pos += tokenLength;
                } else {
                    expandedText.append('^');
                    pos++;
                }
            } else {
                expandedText.append(input.charAt(pos));
                pos++;
            }
        }
        return expandedText.toString();
    }

    public static String unzipFile(String file, int windowSize, boolean readable) {
        String newName = unzipFileName(file);
        if (newName == null) return null;
        String content = FileUtils.readFile(file);
        LZLite lz = new LZLite(windowSize, readable);
        String uncompressed = lz.unzip(content);
        FileUtils.writeFile(newName, uncompressed);
        return newName;
    }

    // DON'T DELETE THE GETTERS! THEY ARE REQUIRED FOR TESTING
    public int getWindowSize() {
        return windowSize;
    }

    public String getSlidingWindow() {
        return slidingWindow;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }
}
