package cc.topicexplorer.commoncrawl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A class to construct a RFC 822 formatted string representation of a date
 *
 * @author Florian Luecke
 */
public class RFC822DateFormatter {
    public static final String RFC_822_DATE_FORMAT_STRING = "E', 'dd' 'MMM' 'yy' 'HH':'mm':'ss' 'Z";

    /**
     * Return an RFC 822 formatted representation of a date.
     *
     * @param date
     *            the date to format
     * @param timeZone
     *            a time zone for normalizing time
     * @return the requested date representation
     */
    public static String getRFCDate(Date date, TimeZone timeZone) {
        // format the date according to RFC 822
        final String RFC_822_DATE_FORMAT_STRING = "E', 'dd' 'MMM' 'yy' 'HH':'mm':'ss' 'Z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(RFC_822_DATE_FORMAT_STRING,
                                                           Locale.ENGLISH);
        dateFormat.setTimeZone(timeZone);
        String dateString = dateFormat.format(date);
        return dateString;
    }

    /**
     * Return an RFC 822 formatted representation of a date.
     * Time is normalized to UTC
     *
     * @param date
     *            the date to format
     * @return the requested date representation
     */
    public static String getRFCDate(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        return getRFCDate(date, timeZone);
    }

}
