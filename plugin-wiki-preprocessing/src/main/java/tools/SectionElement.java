package tools;

public class SectionElement extends Element {

	private final Integer intLevel;

	public SectionElement(String text, Integer posWikitextStart, Integer intLevel, Integer oldID) {
		super(text, posWikitextStart, oldID);
		this.intLevel = intLevel;
	}

	public Integer getIntLevel() {
		return intLevel;
	}

	@Override
	public String getInfosSeparatedInColumns() {

		return "\"" + getText() + getSpace() + getWikiTextStartPosition() + getSpace() + getParsedTextStartPosition()
				+ getSpace() + intLevel + "\"";
	}
}
