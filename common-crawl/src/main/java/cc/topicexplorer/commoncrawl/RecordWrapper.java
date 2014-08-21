package cc.topicexplorer.commoncrawl;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;

public class RecordWrapper {
    private static final String HTTP_HEADER_START     = "HTTP/";
    private static final int    HTTP_HEADER_START_LEN = HTTP_HEADER_START.length();
    private static final String HTTP_HEADER_END       = "\r\n\r\n";
    private String              content               = null;
    private ArchiveRecordHeader header                = null;

    public RecordWrapper(ArchiveRecord rec) {
        this.content = extractRecordBody(rec);
        this.header = rec.getHeader();
    }

    /**
     * Extracts the body from an ArchiveRecord.
     * The body contains the full HTPP response of the server.
     *
     * @param record
     *            the record to extract from
     * @return the requested body
     */
    protected static String extractRecordBody(ArchiveRecord record) {
        try {
            byte[] rawData = IOUtils.toByteArray(record, record.available());
            String content = new String(rawData);
            return content;
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Get the content of the record.
     *
     * @return the requested content
     */
    public String getContent() {
        return content;
    }

    public ArchiveRecordHeader getHeader() {
        return this.header;
    }

    /**
     * Get only the HTTP body.
     *
     * @return the requested body
     */
    public String getHTTPBody() {
        String content = this.getContent();

        if (content.substring(0, HTTP_HEADER_START_LEN).equals(HTTP_HEADER_START)) {
            String body = content.substring(content.indexOf("\r\n\r\n") + 4);
            return body;
        }
        return content;
    }

    /**
     * Get only the HTTP header.
     *
     * @return the requested header
     */
    public String getHTTPHeader() {
        String content = this.getContent();
        int headerStart = content.indexOf(HTTP_HEADER_START);

        if (headerStart == -1) {
            return "";
        }

        int headerEnd = content.indexOf(HTTP_HEADER_END, headerStart);
        String header = content.substring(headerStart, headerEnd);
        return header;
    }
}
