package com.Y3507677;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Path object for a given URI. Stores teh mean and interval boundaries for the path
 */
class Path {

    // [weekend/weekday][intervalId]
    private double[][] means = new double[2][4];
    // [weekend/weekday][intervalId][outlier_lower/outlier_upper]
    private double[][][] intervalBoundaries = new double[2][4][2];
    private String uri;
    // Initialised to 48 hours. k=hour,v=noHits
    private Map<Integer, Double> hits = new HashMap<>(48);
    private int[] weekDaysWeekendsSeen = new int[2];
    private int lastDayOfYear = 0;

    /**
     * Create a new {@code Path} object for a given URI
     *
     * @param uri this {@code Path} corresponds to
     */
    Path(String uri) {
        this.uri = uri;
    }

    /**
     * Increment the number of hits an hour has had
     *
     * @param hour to increment
     */
    void addHit(int hour, Calendar c) {
        if (c.get(Calendar.DAY_OF_YEAR) != lastDayOfYear) {
            lastDayOfYear = c.get(Calendar.DAY_OF_YEAR);

            if (Main.isWeekend(c.get(Calendar.DAY_OF_WEEK))) {
                weekDaysWeekendsSeen[1]++;
            } else {
                weekDaysWeekendsSeen[0]++;
            }
        }

        int day = c.get(Calendar.DAY_OF_WEEK);

        hits.merge(Main.isWeekend(day) ? hour + 24 : hour, 1.0, (a, b) -> a + b);
    }

    /**
     * Calculates and stores the interval boundaries for a given duration and outlier coefficient
     *
     * @param outlierCoefficients alpha
     */
    void calculateIntervalBoundaries(int[][] intervals, double outlierCoefficients) {
        for (Map.Entry<Integer, Double> e : hits.entrySet()) {
            boolean weekend = e.getKey() > 23;
            int hour = weekend ? e.getKey() - 24 : e.getKey();
            int intervalId = Main.getInterval(hour);
            // Temporarily store result to index lower boundary
            intervalBoundaries[weekend ? 1 : 0][intervalId][Main.LOWER_BOUNDARY] += Math.pow(e.getValue() - means[weekend ? 1 : 0][intervalId], 2);
        }

        for (int isWeekend = 0; isWeekend < intervalBoundaries.length; isWeekend++) {
            for (int intervalId = 0; intervalId < intervalBoundaries[isWeekend].length; intervalId++) {
                double sd = Math.sqrt(intervalBoundaries[isWeekend][intervalId][0] / (intervals[intervalId][Main.INTERVAL_DURATION] - 1));
                double mean = means[isWeekend][intervalId];
                double lower = mean - (outlierCoefficients * sd);
                intervalBoundaries[isWeekend][intervalId][Main.LOWER_BOUNDARY] = lower < 0 ? 0 : lower;
                intervalBoundaries[isWeekend][intervalId][Main.UPPER_BOUNDARY] = mean + (outlierCoefficients * sd);
            }
        }
    }

    /**
     * Returns the interval boundaries for a given interval
     * <p>
     * [M-alpha*sd][M+alpha*sd]
     *
     * @param intervalId the interval ordinal
     * @return the calculated interval boundaries
     */
    double[] getIntervalBoundaries(int intervalId, boolean weekend) {
        return intervalBoundaries[weekend ? 1 : 0][intervalId];
    }

    /**
     * Calculate the mean for this path and it's intervals. The mean is the average number of hits per hour for an interval
     */
    void calculateMean() {
        for (Map.Entry<Integer, Double> e : hits.entrySet()) {
            int hour = e.getKey() > 23 ? e.getKey() - 24 : e.getKey();
            int intervalId = Main.getInterval(hour);
            if (e.getValue() != 1) e.setValue(e.getValue() / weekDaysWeekendsSeen[isWeekend(hour)]);
            means[Path.isWeekend(e.getKey())][intervalId] += e.getValue();
        }

        for (int i = 0; i < means.length; i++) {
            for (int j = 0; j < means[i].length; j++) {
                means[i][j] /= (Main.intervals[j][Main.INTERVAL_DURATION]);
            }
        }
    }

    /**
     * Returns whether or not an hour is a weekend. Given the Map structure implemented. If the hour is > 23
     *
     * @param hour to check
     * @return whether or not the hour is a weekend
     */
    private static int isWeekend(int hour) {
        return hour > 23 ? 1 : 0;
    }

    @Override
    public String toString() {
        return "Path{" +
                "uri=" + uri +
                ", means=" + Arrays.deepToString(means) +
                ", intervalBoundaries=" + Arrays.deepToString(intervalBoundaries) +
                '}';
    }
}
