package tools;

public abstract class Element {

	private final String space = "\";\"";
	private String text;
	private PointInteger textPosition;
	private PointInteger posParsedText = new PointInteger(-1, -1);
	private Integer oldId;

	public Element() {

	}

	public Element(String text, Integer posWikitextStart, Integer oldId) {
		setText(text);
		setWikiTextPosition(new PointInteger(posWikitextStart, new Integer(posWikitextStart + text.length())));
		setOldId(oldId);
	}

	public String getSpace() {
		return space;
	}

	public Integer getWikiTextStartPosition() {
		return textPosition.getStartPosition();
	}

	public Integer getWikiTextEndPosition() {
		return textPosition.getEndPosition();
	}

	public PointInteger getWikiTextPoint() {
		return textPosition;
	}

	public void setWikiTextPosition(PointInteger textPointInteger) {
		textPosition = textPointInteger;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setParsedTextPoint(PointInteger posParsedText) {
		this.posParsedText = posParsedText;
	}

	public Integer getParsedTextStartPosition() {
		return posParsedText.getStartPosition();
	}

	public String getInfosSeparatedInColumns() {

		return "\"" + text + getSpace() + getWikiTextStartPosition() + getSpace() + getParsedTextStartPosition() + "\"";
	}

	public Integer getOldId() {
		return oldId;
	}

	public void setOldId(Integer oldId) {
		this.oldId = oldId;
	}

}
