package swearwordsfilter;

import java.util.ArrayList;

public class SwearWordsFilter {

    public static String suppressBadWords(String input, ArrayList<String> badWords) {
        StringBuilder result = new StringBuilder(input);

        for (String badWord : badWords) {
            int startIndex = result.indexOf(badWord);
            while (startIndex != -1) {
                if ((startIndex == 0 || !Character.isLetter(result.charAt(startIndex - 1))) &&
                        (startIndex + badWord.length() == result.length()
                                || !Character.isLetter(result.charAt(startIndex + badWord.length())))) {

                    String suppressed = result.charAt(startIndex) + "*".repeat(badWord.length() - 1);
                    result.replace(startIndex, startIndex + badWord.length(), suppressed);

                    startIndex = result.indexOf(badWord, startIndex + suppressed.length());
                } else {
                    startIndex = result.indexOf(badWord, startIndex + badWord.length());
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        ArrayList<String> badWords = new ArrayList<>();
        badWords.add("bad");
        badWords.add("silly");

        String input = "This is a silly example in a bad test.";
        String processed = suppressBadWords(input, badWords);

        System.out.println("Original: " + input);
        System.out.println("Processed: " + processed);
    }
}
