package cc.topicexplorer.commoncrawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JapaneseCharacterDetector {
    private static final Pattern JAPANESE_CHARACTER_PATTERN = Pattern.compile("[\\p{InHiragana}]+");

    public static boolean containsJapaneseCharacters(String string) {
        if (string == null) {
            string = "";
        }

        Matcher m = JAPANESE_CHARACTER_PATTERN.matcher(string);
        return m.find();
    }
}
