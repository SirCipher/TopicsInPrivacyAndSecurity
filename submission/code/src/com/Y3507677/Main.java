package com.Y3507677;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final String line = "(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})(?: - - )\\[(.*?)] \"(.*?)\" ([0-9]+) ([0-9]+)";
    private static final String uriRegex = "(?: )(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?(?: )";
    private static final Pattern linePattern = Pattern.compile(line);
    private static final Pattern uriPattern = Pattern.compile(uriRegex);
    private static int outlierCoefficient;
    private static SimpleDateFormat format = new SimpleDateFormat("dd/MMMM/yyyy:HH:mm:ss");
    private static Map<String, Path> trainingData = new HashMap<>();
    private static Map<String, Integer> testData = new HashMap<>();
    private static File trainingFile;
    private static File testFile;
    static final int INTERVAL_DURATION = 2, LOWER_BOUNDARY = 0, UPPER_BOUNDARY = 1;
    static int[][] intervals = {{6, 8, 2}, {8, 18, 10}, {18, 22, 4}, {22, 6, 8}};

    interface ParseCallback {
        void call(Calendar c, String[] request);
    }

    private static void init(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Not enough argument specified. Expected: training file, test file, outlier coefficient");
        }

        trainingFile = new File(args[0]);

        if (!trainingFile.exists()) {
            throw new IllegalArgumentException("Training file not found");
        }

        testFile = new File(args[1]);

        if (!testFile.exists()) {
            throw new IllegalArgumentException("Test file not found");
        }

        try {
            outlierCoefficient = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not read outlier coefficient: " + args[2]);
        }

        if (outlierCoefficient <= 0) {
            throw new IllegalArgumentException("Outlier coefficient must be greater than 0");
        }
    }

    /**
     * Run the application with arguments of the following order: training file path, test file path, outlier coefficient alpha > 0
     *
     * @param args training file, test file, outlier coefficient alpha > 0
     */
    public static void main(String[] args) throws IOException {
        init(args);
        train();
        processTrainingData();
        test();
    }

    /**
     * Generate the test data from the test file
     *
     * @throws IOException if there is an issue parsing the training file
     */
    private static void train() throws IOException {
        // Parse and process training data
        parseFile(trainingFile, (c, request) -> {
            String path = request[LogIndexes.URI.index];
            Path match = trainingData.computeIfAbsent(path, k -> new Path(path));
            match.addHit(c.get(Calendar.HOUR_OF_DAY), c);
        });

        System.out.println("Generated training data:\n------------");
    }

    /**
     * Process the test file and report any outliers
     *
     * @throws IOException if there is an issue parsing the testing file
     */
    private static void test() throws IOException {
        final Calendar[] lastDate = {null};

        System.out.println("------------\nTest data:");
        // Parse and process test data
        parseFile(testFile, (c, request) -> {
            if (lastDate[0] == null) {
                lastDate[0] = c;
            }
            // If this is a new hour
            if (c.get(Calendar.HOUR_OF_DAY) != lastDate[0].get(Calendar.HOUR_OF_DAY)) {
                for (Map.Entry<String, Integer> e : testData.entrySet()) {
                    Path trainingMatch = trainingData.get(e.getKey());
                    if (trainingMatch == null) {
                        System.out.println("[" + lastDate[0].getTime() + "] " + e.getKey() + " " + testData.get(e.getKey()) + " [0.0, 0.0]");
                        continue;
                    }

                    int day = lastDate[0].get(Calendar.DAY_OF_WEEK);
                    int intervalId = Main.getInterval(lastDate[0].get(Calendar.HOUR_OF_DAY));
                    double[] boundaries = trainingMatch.getIntervalBoundaries(intervalId, Main.isWeekend(day));

                    if (e.getValue() < boundaries[LOWER_BOUNDARY] || e.getValue() > boundaries[UPPER_BOUNDARY]) {
                        lastDate[0].set(Calendar.MINUTE, 0);
                        lastDate[0].set(Calendar.SECOND, 0);
                        System.out.println("[" + lastDate[0].getTime() + "] " + e.getKey() + " " + testData.get(e.getKey())
                                + " [" + String.format("%.2f, %.2f]", boundaries[LOWER_BOUNDARY], boundaries[UPPER_BOUNDARY]));
                    }
                }
                // Clear off the last hours data
                testData = new HashMap<>();
            } else {
                // Increment the number of hits an hour has had
                testData.merge(request[LogIndexes.URI.index], 1, (a, b) -> a + b);
            }
            lastDate[0] = c;
        });

    }

    /**
     * Generate the mean and interval boundaries for all the paths
     */
    private static void processTrainingData() {
        for (Map.Entry<String, Path> e : trainingData.entrySet()) {
            Path p = e.getValue();
            p.calculateMean();
            p.calculateIntervalBoundaries(intervals, outlierCoefficient);
            System.out.println(p);
        }
    }

    /**
     * Parse a log file line and return it's components
     *
     * @param line to parse
     * @return the components of the line
     */
    private static String[] parseLogLine(String line) {
        Matcher lineMatcher = linePattern.matcher(line);
        String[] result = new String[LogIndexes.values().length];

        while (lineMatcher.find()) {
            // 0 Returns the full match
            for (int i = 1; i <= lineMatcher.groupCount(); i++) {
                String s = lineMatcher.group(i);
                // Extract the URI from the log line
                if (i - 1 == LogIndexes.URI.index) {
                    Matcher uriMatcher = uriPattern.matcher(s);
                    boolean found = false;
                    while (uriMatcher.find() && !found) {
                        for (int j = 1; j <= uriMatcher.groupCount(); j++) {
                            String p = uriMatcher.group(j);
                            if (p != null && !"".equals(p)) {
                                s = uriMatcher.group(j).trim();
                                found = true;
                                break;
                            }
                        }
                    }
                }
                result[i - 1] = s;
            }
        }

        return result;
    }

    /**
     * Parse a given file and call the provided {@code ParseCallback} once the line has been parsed
     *
     * @param file     to read
     * @param callback to call once the line has been parsed
     * @throws IOException if there is an issue parsing the file
     */
    private static void parseFile(File file, ParseCallback callback) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); Scanner sc = new Scanner(inputStream, "UTF-8")) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] request = parseLogLine(line);
                Date date;

                try {
                    date = format.parse(request[LogIndexes.DATE.index]);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Could not parse date: " + request[LogIndexes.DATE.index], e);
                }

                Calendar c = Calendar.getInstance();
                c.setTime(date);
                callback.call(c, request);
            }
        }
    }

    /**
     * Returns if the given day is a weekend
     *
     * @param day to check
     * @return if it is a weekend
     */
    static boolean isWeekend(int day) {
        return !(Calendar.MONDAY <= day && day <= Calendar.FRIDAY);
    }

    /**
     * Returns the interval that an hour resides in
     *
     * @param hour to find the interval of
     * @return the interval id
     */
    static int getInterval(int hour) {
        if (hour >= 6 && hour < 8) {
            return 0;
        } else if (hour >= 8 && hour < 18) {
            return 1;
        } else if (hour >= 18 && hour < 22) {
            return 2;
        } else {
            // Hour is between 22:00 and 06:00
            return 3;
        }
    }
}
