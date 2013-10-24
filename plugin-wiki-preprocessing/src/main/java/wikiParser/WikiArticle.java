package wikiParser;
public class WikiArticle
{

	private String parsedWikiText;
	private Integer old_id;
	private int failureId;
	private String wikiOrigText ;
	private String wikiTitle;
	private String parsedWikiTextReadable;

	
	/**
	 * 
	 * @param parsedWikiTxt
	 * @param old_id
	 * @param wikiOrigText
	 * @param wikiTitle
	 * @param failureId
	 */
	public WikiArticle(String parsedWikiTxt, Integer old_id , String wikiOrigText, String wikiTitle, int failureId , String parsedWikitextReadable)
	{
		this.setparsedWikiTxt(parsedWikiTxt);
		this.setOldID(old_id);
		this.setWikiOrigText(wikiOrigText);
		this.setWikiTitle(wikiTitle);
		this.setFailureID(failureId);
		this.setparsedWikiTxtReadable(parsedWikitextReadable);		
	}

	private void setWikiTitle(String wikiTitle)
	{
		this.wikiTitle = wikiTitle;
	}
	
	public String getWikiTitle()
	{
		return wikiTitle;
	}
	
	private void setWikiOrigText(String wikiOrigText)
	{
		this.wikiOrigText= wikiOrigText;
	}
	
	public String getWikiOrigText()
	{
		return wikiOrigText;
	}

	private void setparsedWikiTxt(String wikiTxt)
	{
		this.parsedWikiText = wikiTxt;
	}

	public String getParsedWikiText()
	{
		return parsedWikiText;
	}

	private void setOldID(Integer old_id)
	{
		this.old_id = old_id;
	}

	public Integer getOldID()
	{
		return old_id;
	}

	private void setFailureID(int failureId)
	{
		this.failureId = failureId;
	}

	public int getFailureId()
	{
		return failureId;
	}

	private void setparsedWikiTxtReadable(String wikiTxt)
	{
		this.parsedWikiTextReadable = wikiTxt;
	}

	public String getParsedWikiTextReadable()
	{
		return parsedWikiTextReadable;
	}
}