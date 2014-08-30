package cc.topicexplorer.commoncrawl;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


class HelperUtils {

    // TODO: write test cases
    public static List<String> loadFileAsArray(String path) throws IOException {
            String contents = FileUtils.readFileToString(new File(path));
            String[] lines = contents.split("\n");
            return Arrays.asList(lines);
    }
}

