package com.Y3507677;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tests {

    private static final String loggerRegex = "(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})(?: - - )\\[(.*?)] \"(.*?)\" ([0-9]+) ([0-9]+)";
    private static final String pathRegex = "(?: )(([^:\\/?#]+):)?(\\/\\/([^\\/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?(?: )";

    private static final Pattern loggerPattern = Pattern.compile(loggerRegex);
    private static final Pattern pathPattern = Pattern.compile(pathRegex);

    private static final String logLine = "124.57.77.113 - - [07/Nov/2018:23:17:50 0000] \"GET /mailman/listinfo/students HTTP/1.1\" 200 6338";

    @Test
    public void testLoggerPattern() {
        Matcher matcher = loggerPattern.matcher(logLine);

        while (matcher.find()) {
            // 0 Returns the full line
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println(matcher.group(i));
            }
        }
    }

    @Test
    public void testPathPattern() {
        Matcher matcher = loggerPattern.matcher(logLine);
        String[] result = new String[5];

        while (matcher.find()) {
            // 0 Returns the full line
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String s = matcher.group(i);

                if (i - 1 == 2) {
                    Matcher pathMatcher = pathPattern.matcher(s);
                    while (pathMatcher.find()) {
                        // 0 Returns the full line
                        for (int j = 1; j <= pathMatcher.groupCount(); j++) {
                            String p = pathMatcher.group(j);
                            if (p != null && !"".equalsIgnoreCase(p)) {
                                s = pathMatcher.group(j).trim();
                            }
                        }
                    }
                }
                result[i - 1] = s;
            }
        }
    }

    @Test
    public void parseDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MMMM/yyyy:HH:mm:ss");

        Date date = format.parse("07/Nov/2018:23:29:55 0000");
        System.out.println(date.toString());
    }
}
