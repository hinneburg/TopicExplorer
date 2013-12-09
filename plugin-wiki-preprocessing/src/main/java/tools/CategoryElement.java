package tools;

public class CategoryElement extends Element {

	private String title;

	public CategoryElement() {
		// String text, Integer posWikitextStart, Integer oldId, String
		// pageTitle) {
		// super(text, posWikitextStart, oldId);
		// title = pageTitle;
	};

	@Override
	public String getInfosSeparatedInColumns() {

		return "\"" + getOldId() + getSpace() + title + getSpace()
				+ ExtraInformations.getTargetWithoutCategoryInformation(getText()) + "\"";
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
