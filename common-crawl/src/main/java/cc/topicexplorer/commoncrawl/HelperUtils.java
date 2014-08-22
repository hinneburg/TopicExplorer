package cc.topicexplorer.commoncrawl;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;


class HelperUtils {

    // TODO: write test cases
    public static String[] loadFileAsArray(String path) throws IOException {
            String contents = FileUtils.readFileToString(new File(path));
            String[] lines = contents.split("\n");
            return lines;
    }
}

