package tools;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WikiTextToCSVForewardIntegrationTest {
	private WikiTextToCSVForeward wikiTestToCSVForeward;

	private final String path = "src/test/resources/";

	private final String parsedWikiTxt = TestUtil.returnFileAsString(this.path + "outputParsed.txt");
	private final String inputWikiOriginal = TestUtil.returnFileAsString(this.path + "inputOrig.txt");
	private final String parsedWikiTextReadable = TestUtil.returnFileAsString(this.path + "readableText.txt");
	private Integer old_id;
	private String wikiTitle;
	private WikiArticle wikiArticle;

	@Before
	public void init() {

		old_id = 38;
		wikiTitle = "Konrad_Zuse";

		wikiArticle = new WikiArticle(parsedWikiTxt, old_id, inputWikiOriginal, wikiTitle, 0, parsedWikiTextReadable);
		wikiTestToCSVForeward = new WikiTextToCSVForeward(wikiArticle, null, true, false);
	}

	@Ignore
	@Test
	public void testGetPictures() {

		String test = TestUtil.returnFileAsString(this.path + "picturePositions.txt");
		assertThat(wikiTestToCSVForeward.getPictures()).isEqualTo(test);
	}

	@Ignore
	@Test
	public void testGetLinks() {

		String test = TestUtil.returnFileAsString(this.path + "linkPositions.txt");
		assertThat(wikiTestToCSVForeward.getLinkInfos()).isEqualTo(test);
	}

	@Ignore
	@Test
	public void testGetSections() {

		String test = TestUtil.returnFileAsString(this.path + "sectionPositions.txt");
		assertThat(wikiTestToCSVForeward.getSectionCaptions()).isEqualTo(test);
	}

	@Ignore
	@Test
	public void testGetCategory() {

		String test = TestUtil.returnFileAsString(this.path + "categoryPositions.txt");
		assertThat(wikiTestToCSVForeward.getCategroryInfos()).isEqualTo(test);
	}

}
