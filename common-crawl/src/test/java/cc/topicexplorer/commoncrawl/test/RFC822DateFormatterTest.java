package cc.topicexplorer.commoncrawl.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

import cc.topicexplorer.commoncrawl.RFC822DateFormatter;

public class RFC822DateFormatterTest {
    @Test
    public void testGetRFCDate() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(2002, 9, 02, 15, 0, 0); // month is 0 based wtf??
        Date date = calendar.getTime();
        String dateRFC = RFC822DateFormatter.getRFCDate(date);

        assertEquals("Wed, 02 Oct 02 15:00:00 +0000", dateRFC);
    }
}
