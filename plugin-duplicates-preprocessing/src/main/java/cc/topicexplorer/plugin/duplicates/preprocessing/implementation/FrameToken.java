package cc.topicexplorer.plugin.duplicates.preprocessing.implementation;

public class FrameToken {

	private int position;
	private String token;
	
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
	
	public FrameToken( int position, String token )
	{
		this.position = position;
		this.token = token;
	}
	
}
