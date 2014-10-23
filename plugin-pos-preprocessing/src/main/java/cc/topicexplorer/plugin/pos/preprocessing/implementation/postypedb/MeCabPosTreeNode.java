package cc.topicexplorer.plugin.pos.preprocessing.implementation.postypedb;

import java.util.ArrayList;
import java.util.Iterator;

public class MeCabPosTreeNode {

	private int posID;
	private String posName;
	
	private int dbID;

	private int low, high;
	
	private ArrayList<MeCabPosTreeNode> childNodes = new ArrayList<MeCabPosTreeNode>();


	private MeCabPosTreeNode parentNode = null; // default

	
	
	
	

	public void setHigh(int high) {
		this.high = high;
	}
	
	public void setLow(int low) {
		this.low = low;
	}
	


	public int getDbID() {
		return dbID;
	}

	public void setDbID(int dbID) {
		this.dbID = dbID;
	}

	

	public MeCabPosTreeNode getChild ( int index )
	{
		if ( index >= 0 && index < this.childrenCount() )
		{
			return this.childNodes.get(index);
		}
		
		return null;
	}

	public MeCabPosTreeNode getChild( String childName )
	{
		Iterator<MeCabPosTreeNode> i = this.childNodes.iterator();
		
		while ( i.hasNext() )
		{
			MeCabPosTreeNode currentNode = i.next();
			
			if ( currentNode.getPosName().equals(childName) )
				return currentNode;
		}
		
		return null;
	}


	public int getHigh() {
		return high;
	}


	public int getLow() {
		return low;
	}

	
	public MeCabPosTreeNode getParentNode() {
		return parentNode;
	}

	
	public int getPosID() {
		return posID;
	}
	
	public String getPosName() {
		return posName;
	}
	
	
	
	
	
	
	

	public MeCabPosTreeNode(int posID, String posName, int low, int high) {
		super();
		init(posID, posName, low, high);
	}


	public MeCabPosTreeNode(int posID, String posName, int low, int high, MeCabPosTreeNode parentNode) {
		super();
		init(posID, posName, low, high);
		this.parentNode = parentNode;
	}

	

	private void init(int posID, String posName, int low, int high) {
		this.posID = posID;
		this.posName = posName;
		this.setLow(low);
		this.setHigh(high);
	}
	
	
	
	
	
	
	
	

	public void addChildNode( MeCabPosTreeNode childNode )
	{
		this.childNodes.add(childNode);
		
		this.selfRangeUpdate(childNode.getLow(), childNode.getHigh());
		
		this.notifyParentNodeAboutRangeUpdate();
	}


	public int childrenCount()
	{
		return this.childNodes.size();
	}
	
	public boolean containsChild( String childName )
	{
		Iterator<MeCabPosTreeNode> i = this.childNodes.iterator();
		
		while ( i.hasNext() )
		{
			MeCabPosTreeNode currentNode = i.next();
			
			if ( currentNode.getPosName().equals(childName) )
				return true;
		}
		
		return false;
	}
	
	private void notifyParentNodeAboutRangeUpdate()
	{
		if ( this.parentNode == null )
			return;
		
		this.parentNode.receiveRangeNotificationFromChild(this.getLow(), this.getHigh());
	}
	
	
	
	public void receiveRangeNotificationFromChild( int low, int high )
	{
		this.selfRangeUpdate(low, high);
		
		this.notifyParentNodeAboutRangeUpdate();
	}
	

	private void selfRangeUpdate( int low, int high )
	{
		if ( low < this.getLow() )
			this.setLow(low);
		if ( high > this.getHigh() )
			this.setHigh(high);
	}
	
	
	
}
