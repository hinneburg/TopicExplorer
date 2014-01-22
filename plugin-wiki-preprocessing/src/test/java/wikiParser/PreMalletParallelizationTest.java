package wikiParser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import tools.TestUtil;
import tools.WikiIDTitlePair;

public class PreMalletParallelizationTest {

	private final String path = "src/test/resources/";
	private final PreMalletParallelization pm = new PreMalletParallelization();
	// private String textReadable = TestUtil.returnFileAsString(this.path +
	// "readableText.txt");;

	private final String articleTextUncorrected = "[[Text]] <!-- [[commentedLink]] --> [[Datei:name.jpg|miniatur|text mit verschachtelten [[link]] bla bla ]]  ";
	private final String articleTextCorrected = "[[Text|Text]]  [[Datei:name.jpg|miniatur|text mit verschachtelten [[link|link]] bla bla ]]  ";

	private final String inputWikiOriginal = TestUtil.returnFileAsString(this.path + "2_inputOrig.txt");
	private final String parsedWikiTextReadable = TestUtil.returnFileAsString(this.path + "2_readableText.txt");
	private final String outputParsed = TestUtil.returnFileAsString(this.path + "2_outputParsed.txt");

	@Test
	public void testCorrectWikitext() {

		String test = this.articleTextCorrected;
		assertThat(this.pm.doArticleCorrection(articleTextUncorrected, new WikiIDTitlePair(0, ""))).isEqualTo(test);
	}

	@Test
	public void testParsedTextReadable() {

		// FIXME russisch, zeilenende vom buffered reader macht Probleme
		String test = this.parsedWikiTextReadable;
		if (test.endsWith("\n")) {
			test = test.substring(0, test.length() - 1);
		}
		String test2;
		try {

			test2 = this.pm.parse(inputWikiOriginal, new WikiIDTitlePair(0, "test"), false);

			assertThat(test2).isEqualTo(test);

		} catch (Exception e) {
			System.err.println("Failure in test");
		}

	}

	@Test
	public void testParsedTextByLine() {

		// FIXME russisch, zeilenende vom buffered reader macht Probleme
		String test = this.outputParsed;
		if (test.endsWith("\n")) {
			test = test.substring(0, test.length() - 1);
		}
		String test2;

		try {
			test2 = this.pm.parse(inputWikiOriginal, new WikiIDTitlePair(0, "test"), true);

			assertThat(test2).isEqualTo(test);

		} catch (Exception e) {
			System.err.println("Failure in test");
		}

	}
}