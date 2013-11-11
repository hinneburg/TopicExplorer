package tools;

//import java.util.Iterator;

public class WikiIDTitlePair
{

	private final String wikiTitle;
	private final Integer old_id;

	public WikiIDTitlePair(Integer old_id, String wikiTitle)
	{

		this.wikiTitle = wikiTitle;
		this.old_id = old_id;
	}

	public Integer getOld_id()
	{
		return old_id;
	}

	public String getWikiTitle()
	{
		return wikiTitle;
	}
}
