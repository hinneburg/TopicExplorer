package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestUtil {

	public static String returnFileAsString(String path) {
		StringBuilder stringBuilder = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = null;
			String lineSeparator = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(lineSeparator);
			}
			reader.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return stringBuilder.toString();
	}
}
