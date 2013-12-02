package tools;

public class TokenElement {

	private String token;
	private String term;
	private Integer posReadableText;
	private Integer posWikitext;
	private PointInteger posPointInteger;
	private boolean isLink;
	private boolean isCaption;
	private Integer captionTier;

	public TokenElement(String token, String term, Integer posOriginal, Integer posWikitext) {
		this.token = token;
		this.term = term;
		this.posReadableText = posOriginal;
		this.posWikitext = posWikitext;
	}

	public String getToken() {
		return token;
	}

	public String getTerm() {
		return term;
	}

	public Integer getPosReadableText() {
		return posReadableText;
	}

	public Integer getPosWikitext() {
		return posWikitext;
	}

	public PointInteger getPosReadableTextPointInteger() {
		return new PointInteger(posReadableText, new Integer(posReadableText + token.length()));
	}

}
