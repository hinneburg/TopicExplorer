package cc.topicexplorer.commoncrawl.test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCRecord;
import org.junit.Test;

import cc.topicexplorer.commoncrawl.RecordWrapper;

public class RecordWrapperTest {
    private static final String TEST_RECORD_NAME = "/TestRecord.warc";
    private static final String TEST_RECORD_PATH = RecordWrapperTest.class.getResource(TEST_RECORD_NAME).getPath();

    private static ArchiveRecord makeNewRecord() throws FileNotFoundException,
        IOException {
        FileInputStream f = new FileInputStream(TEST_RECORD_PATH);
        ArchiveRecord rec = new WARCRecord(f, "Test Record", 0);
        return rec;
    }
    
    @Test
    public void testGetHeader() throws FileNotFoundException, IOException {
        ArchiveRecord record = makeNewRecord();
        RecordWrapper w = new RecordWrapper(record);
        assertEquals(record.getHeader(), w.getHeader());
        assertEquals(record.getHeader(), w.getHeader());
    }

    @Test
    public void testGetHTTPBody() throws FileNotFoundException, IOException {
        ArchiveRecord record = makeNewRecord();
        RecordWrapper w = new RecordWrapper(record);
        assertEquals(w.getHTTPBody(), "12345");

        // the reason the wrapper exists is so that we can
        // get the contents multiple times. so try at least twice
        assertEquals(w.getHTTPBody(), "12345");
    }

    @Test
    public void testGetHTTPHeader() throws FileNotFoundException, IOException {
        ArchiveRecord record = makeNewRecord();
        RecordWrapper w = new RecordWrapper(record);
        assertEquals(w.getHTTPHeader(),
                     "HTTP/1.1 200 OK\r\n"
                             + "Server: nginx/1.6.0\r\n"
                             + "Date: Wed, 09 Jul 2014 23:30:51 GMT\r\n"
                             + "Content-Type: text/plain; charset=UTF-8");

        // the reason the wrapper exists is so that we can
        // get the contents multiple times. so try at least twice
        assertEquals(w.getHTTPHeader(),
                     "HTTP/1.1 200 OK\r\n"
                             + "Server: nginx/1.6.0\r\n"
                             + "Date: Wed, 09 Jul 2014 23:30:51 GMT\r\n"
                             + "Content-Type: text/plain; charset=UTF-8");
    }
}
