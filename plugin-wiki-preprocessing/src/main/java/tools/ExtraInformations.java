package tools;

public class ExtraInformations {

	public static final String extraInternalLink = "_isLinkIsLink_";
	public static final String extraBoldAppend = "_isBoldIsBold_";
	public static final String extraCursiveAppend = "_isCursiveIsCursive_";
	public static final String extraPicure1Append = "_isPicture1IsPicture1_";
	public static final String extraPicure2Append = "_isPicture2IsPicture2_";
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

	public static final String extraFileNameJapan = "File:"; // TODO japanisches
																// File

	public static boolean getIsPictureStartsWith(String parsedLine) {
		Boolean output = false;

		if (parsedLine.startsWith(ExtraInformations.extraFileNameGerman)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameEnglish)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameEnglishSecond)
				|| parsedLine.startsWith(ExtraInformations.extraFileNameJapan)) {
			output = true;
		}

		return output;
	}
}
