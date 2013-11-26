package tools;

public class LinkElement {

	public LinkElement() {
	}

	private String target;
	private String text;
	private PointInteger completeLink;
	private PointInteger linkText;
	private Integer pipePosition;
	private PointInteger posParsedText;

	public Integer getpipePosition() {
		return pipePosition;
	}

	public String getTarget() {
		return target;
	}

	public void setTargetString(String target) {
		this.target = target;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public PointInteger getCompleteLinkSpan() {
		return completeLink;
	}

	public Integer getCompleteLinkStartPosition() {
		return completeLink.getStartPoint();
	}

	public Integer getCompleteLinkEndPosition() {
		return completeLink.getEndPoint();
	}

	public void setCompleteLinkSpan(PointInteger completeLink) {
		this.completeLink = completeLink;
	}

	public PointInteger getLinkText() {
		return linkText;
	}

	public Integer getLinkTextStart() {
		return linkText.getStartPoint();
	}

	public Integer getLinkTextEnd() {
		return linkText.getEndPoint();
	}

	public void setLinkTextPosition(PointInteger linkText) {
		this.linkText = linkText;
		this.pipePosition = linkText.getStartPoint();
	}

	public Integer getPosParsedTextStartPoint() {
		return posParsedText.getStartPoint();
	}

	public Integer getPosParsedTextEndPoint() {
		return posParsedText.getEndPoint();
	}

	public void setPosParsedText(PointInteger posParsedText) {
		this.posParsedText = posParsedText;
	}

	public String getInfosSeparatedInColumns() {
		String space = "\" ; \"";
		return "\"" + target + space + text + space + getCompleteLinkStartPosition() + space + getpipePosition()
				+ space + getCompleteLinkEndPosition() + "\"";
		// + space + getPosParsedTextStartPoint + space +
		// getPosParsedTextEndPoint + "\"";
	}

}
