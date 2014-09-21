package cc.topicexplorer.commoncrawl.test;

import static org.junit.Assert.*;
import cc.topicexplorer.commoncrawl.HelperUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class HelperUtilsTest {
    @Test
    public void testJoin() {
        String[] lines = new String[] { "1", "2", "3" };
        List<String> lineList = Arrays.asList(lines);
        String joinedString = HelperUtils.join(lineList, ",");
        assertEquals("1,2,3", joinedString);

        lines = new String[] { "", "", "" };
        lineList = Arrays.asList(lines);
        joinedString = HelperUtils.join(lineList, ",");
        assertEquals(",,", joinedString);

        lineList = new ArrayList<String>(0);
        joinedString = HelperUtils.join(lineList, ",");
        assertEquals("", joinedString);
    }

    @Test
    public void testGetTopPrivateDomain() {
        String url = "https://images.google.com/?gws_rd=ssl";
        String domain = HelperUtils.getTopPrivateDomain(url);
        assertEquals("google.com", domain);

        url = "http://docs.oracle.com/javase/6/docs/api/java/net/URL.html#getHost()";
        domain = HelperUtils.getTopPrivateDomain(url);
        assertEquals("oracle.com", domain);
    }
}
