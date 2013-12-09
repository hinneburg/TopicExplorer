package tools;

public class ExtraInformations {

	public static final String extraInternalLink = "_isLinkIsLink_";
	public static final String extraBoldAppend = "_isBoldIsBold_";
	public static final String extraCursiveAppend = "_isCursiveIsCursive_";
	// comes from wtimagelink part of the target
	public static final String extraPicure1Append = "_isPicture1IsPicture1_";
	// comes from wtimagelink part of the title
	public static final String extraPicure2Append = "_isPicture2IsPicture2_";
	// comes from text starts with file
	public static final String extraPicure3Append = "_isPicture3IsPicture3_";

	// comes from internal link , starts with filename
	public static final String extraPicure4Append = "_isPicture4IsPicture4_";
	// comes from internal link, second part, covers the textpart from the
	// "link"
	public static final String extraPicure5Append = "_isPicture5IsPicture5_";

	public static final String extraOptionalForReadableAppend = "_isOptionalAndDoesNotGetAnyConsideration_"; // within
																												// readable
																												// tokenisation

	public static final String extraSectionCaptionAppend = "_isSectionCaptionisSectionCaption_";
	public static final String extraSectionLevelStart = "_hasLevel";
	public static final String extraSectionLevelEnd = "hasLevel_";

	public static final String extraFileNameGerman = "Datei:";
	public static final String extraFileNameEnglish = "File:";
	public static final String extraFileNameEnglishSecond = "Image:";

	public static final String extraFileNameJapan = "画像";
	public static final String extraFileNameJapan2 = "ファイル";

	public static final String catGerman = "Kategorie:";
	public static final String catEnglish = "Category:"; // for japan it is the
															// same

	public static boolean getIsPictureStartsWith(String parsedLine) {
		Boolean output = false;

		if (parsedLine.startsWith(ExtraInformations.extraFileNameGerman)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameEnglish)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameEnglishSecond)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameJapan)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameJapan2)) {
			output = true;
		}

		return output;
	}

	public static boolean getIsCategory(String linkTarget) {
		Boolean output = false;

		if (linkTarget.startsWith(catGerman) || linkTarget.startsWith(catEnglish)) {
			output = true;
		}
		return output;
	}

	public static String getTargetWithoutCategoryInformation(String linkTarget) {
		String output = "";

		if (linkTarget.startsWith(catGerman)) {
			output = linkTarget.replace(catGerman, "");
		} else if (linkTarget.startsWith(catEnglish)) {
			output = linkTarget.replace(catEnglish, "");
		}
		return output;
	}

}
