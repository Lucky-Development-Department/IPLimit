import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherTest {

    public static void main(String[] args) {
        String key = "127.0.0.1";
        Matcher comma = Pattern.compile("\\.").matcher("");

        System.out.println(comma.reset(key).replaceAll(","));
    }
}
