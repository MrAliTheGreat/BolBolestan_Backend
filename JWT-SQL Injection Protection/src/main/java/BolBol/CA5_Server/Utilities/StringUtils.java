package BolBol.CA5_Server.Utilities;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

    public static String quoteWrapper(String str) {
        return "'" + str + "'";
    }

    public static String hashPassword(String password){
        return Integer.toString(password.hashCode());
    }

    public static String stripTags(String str) {
        String stripStr = str;
        List<String> illegalWords = Arrays.asList("\\<.*?\\>", "'", "\"" , "%", "=" , "$" , "&");
        for (String illegalWord: illegalWords) {
            stripStr = stripStr.replaceAll(illegalWord, "");
        }
        stripStr = StringEscapeUtils.escapeSql(stripStr);
        return stripStr;
    }

    public static boolean hasIllegalChars(String str) {
        return !stripTags(str).equals(str);
    }
}
