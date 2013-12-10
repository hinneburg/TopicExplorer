package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;

public class Frame {

	
	private String md5Hash;
	private int docID, startPos, endPos;
	
	
	public Frame(String md5Hash, int docID, int startPos, int endPos) {
		super();
		this.md5Hash = md5Hash;
		this.docID = docID;
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	
	
	public String getMD5Hash() {
		return md5Hash;
	}
	public void setMd5Hash(String md5Hash) {
		this.md5Hash = md5Hash;
	}
	public int getStartPos() {
		return startPos;
	}
	public int getDocID() {
		return docID;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	public int getEndPos() {
		return endPos;
	}
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docID;
		result = prime * result + endPos;
		result = prime * result + ((md5Hash == null) ? 0 : md5Hash.hashCode());
		result = prime * result + startPos;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Frame other = (Frame) obj;
		if (docID != other.docID)
			return false;
		if (endPos != other.endPos)
			return false;
		if (md5Hash == null) {
			if (other.md5Hash != null)
				return false;
		} else if (!md5Hash.equals(other.md5Hash))
			return false;
		if (startPos != other.startPos)
			return false;
		return true;
	}
	
	
}
