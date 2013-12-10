package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;



/**
 * !!! THIS CLASS IS NOT USED (YET?) !!!
 * 
 * @author user
 *
 */
public class Duplicate {

	private Frame frame;
	private String groupHash;
	
	
	public Frame getFrame() {
		return frame;
	}
	public void setFrame(Frame frame) {
		this.frame = frame;
	}
	public String getGroupHash() {
		return groupHash;
	}
	public void setGroupHash(String groupHash) {
		this.groupHash = groupHash;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((frame == null) ? 0 : frame.hashCode());
		result = prime * result
				+ ((groupHash == null) ? 0 : groupHash.hashCode());
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
		Duplicate other = (Duplicate) obj;
		if (frame == null) {
			if (other.frame != null)
				return false;
		} else if (!frame.equals(other.frame))
			return false;
		if (groupHash == null) {
			if (other.groupHash != null)
				return false;
		} else if (!groupHash.equals(other.groupHash))
			return false;
		return true;
	}
	
	
}
