package wikiParser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import tools.TestUtil;
import tools.WikiIDTitlePair;

public class PreMalletParallelizationTest {

	private String path = "src/test/resources/";
	private PreMalletParallelization pm = new PreMalletParallelization();
	// private String textReadable = TestUtil.returnFileAsString(this.path +
	// "readableText.txt");;

	private String articleTextUncorrected = "[[Text]] <!-- [[commentedLink]] --> [[Datei:name.jpg|miniatur|text mit verschachtelten [[link]] bla bla ]]  ";
	private String articleTextCorrected = "[[Text|Text]]  [[Datei:name.jpg|miniatur|text mit verschachtelten [[link|link]] bla bla ]]  ";

	private String inputWikiOriginal = TestUtil.returnFileAsString(this.path + "2_inputOrig.txt");
	private String parsedWikiTextReadable = TestUtil.returnFileAsString(this.path + "2_readableText.txt");
	private String outputParsed = TestUtil.returnFileAsString(this.path + "2_outputParsed.txt");

	@Test @Ignore
	public void testCorrectWikitext() {

		String test = this.articleTextCorrected;
		assertThat(this.pm.doArticleCorrection(articleTextUncorrected, new WikiIDTitlePair(0, ""))).isEqualTo(test);
	}

	@Test @Ignore
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

	@Test @Ignore
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