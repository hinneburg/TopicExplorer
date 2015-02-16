package cc.topicexplorer.plugin.mecab.preprocessing.implementation.postagger;

public class JPOSToken
{


	private int documentID, posOfTokenInDocument;
	private String term, token;
	private int posID;

	private int continuation = 0;

	
	public int getPosID() {
		return posID;
	}

	public int getContinuation() {
		return continuation;
	}

	public void setContinuation(int continuation) {
		this.continuation = continuation;
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public int getPosOfTokenInDocument() {
		return posOfTokenInDocument;
	}

	public void setPosOfTokenInDocument(int posOfTokenInDocument) {
		this.posOfTokenInDocument = posOfTokenInDocument;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	public JPOSToken(int documentID, int posOfTokenInDocument, String term,
			String token, int posID) {
		super();
		this.documentID = documentID;
		this.posOfTokenInDocument = posOfTokenInDocument;
		this.term = term;
		this.token = token;
		this.posID = posID;
	}
	
	
	public void increaseContinuation()
	{
		this.continuation++;
	}
	

	
	
	public String csvString()
	{
		return "\"" + this.documentID + "\",\"" + this.posOfTokenInDocument + "\",\""
				+ this.term + "\",\"" + this.token + "\",\"" + this.posID + "\",\"" + this.continuation + "\"";
	}
	
}
