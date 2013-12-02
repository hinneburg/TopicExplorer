package tools;

public class LinkElement extends Element {

	public LinkElement() {
	}

	private String target;
	private PointInteger completeLink;

	public String getTarget() {
		return target;
	}

	public void setTargetString(String target) {
		this.target = target;
	}

	public PointInteger getCompleteLinkSpan() {
		return completeLink;
	}

	public Integer getCompleteLinkStartPosition() {
		return completeLink.getStartPosition();
	}

	public Integer getCompleteLinkEndPosition() {
		return completeLink.getEndPosition();
	}

	public void setCompleteLinkSpan(PointInteger completeLink) {
		this.completeLink = completeLink;
	}

	/**
	 * 
	 * @return csv format : link-target, link-text, startposition of linktarget
	 *         in original text, startposition of linktext in original text,
	 *         startposition of parsed text in normalized text
	 */
	@Override
	public String getInfosSeparatedInColumns() {
		// String space = "\" ; \"";

		return "\"" + target + getSpace() + super.getText() + getSpace() + getCompleteLinkStartPosition() + getSpace()
				+ super.getWikiTextStartPosition() + getSpace() + super.getParsedTextStartPosition() + "\"";
	}

}
