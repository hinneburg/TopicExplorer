package cc.topicexplorer.plugin.pos.preprocessing.implementation.postypedb;

import java.util.ArrayList;
import java.util.Iterator;

public class MeCabPosTree {

	
	private int rangeIndex = 0;
	private final int rangeStep = 1000;
	
	private int supplementaryPosID = 100;
	
	private MeCabPosTreeNode rootNode;
	
	public MeCabPosTree()
	{
		this.rootNode = new MeCabPosTreeNode(-1, "root", rangeIndex, rangeStep-1);
	}
	
	
	
	public void addCompleteBranch( ArrayList<String> nodeNames, int posID )
	{
		MeCabPosTreeNode currentNode = this.rootNode;
		
		Iterator<String> i = nodeNames.iterator();
		int nodeNamesIndex = 0;
		
		while ( i.hasNext() )
		{
			String currentNodeName = i.next();
			nodeNamesIndex++;
			
			MeCabPosTreeNode newCurrentNode = currentNode.getChild(currentNodeName);
			
			if ( newCurrentNode != null )
				currentNode = newCurrentNode;
			else
			{
				// only insert posID if we're at the last element from nodeNames
				// (as there's no posID information about the parent nodes at this point)
				int actualPosID = ( nodeNamesIndex == nodeNames.size()) ? posID : this.supplementaryPosID++;
				
				MeCabPosTreeNode childNode = new MeCabPosTreeNode(actualPosID, currentNodeName, rangeIndex, rangeIndex + rangeStep-1, currentNode);
				currentNode.addChildNode( childNode );
				
				currentNode = childNode;
				
				// update range
				rangeIndex += rangeStep;
			}
			
		}
		
	}
	
	
	@Deprecated
	private void printOutOld( MeCabPosTreeNode node, String output )
	{
		// dirty
		if ( !node.getPosName().equals("root") )
			output += " " + node.getPosName();

		System.out.println("node: " + output + ", low-high: " + node.getLow() + "-" + node.getHigh() + ", \t posID: " + node.getPosID() + "\t parent: " +
		(node.getParentNode() != null ? node.getParentNode().getPosName() + " (posID: " + node.getParentNode().getPosID() + ")" : "null")
				);

		
		if ( node.childrenCount() == 0 )
		{
			return;
		}
		
		for ( int i = 0; i < node.childrenCount(); i++ )
		{
			this.printOutOld(node.getChild(i), output);
		}
	}
	
	public ArrayList<MeCabPosTreeNode> getNodeList()
	{
		ArrayList<MeCabPosTreeNode> result = new ArrayList<MeCabPosTreeNode>();

		result.addAll( this.getNodeList( this.rootNode ) );
		
		return result;
	}
	
	private ArrayList<MeCabPosTreeNode> getNodeList( MeCabPosTreeNode node )
	{
		ArrayList<MeCabPosTreeNode> result = new ArrayList<MeCabPosTreeNode>();

		result.add(node);
		
		
		for ( int i = 0; i < node.childrenCount(); i++ )
		{
			result.addAll( this.getNodeList( node.getChild(i) ) );
		}
		
		return result;
	}

	@Deprecated
	public void printOutOld()
	{
		this.printOutOld( this.rootNode, "" );
	}
	
	
	public void printOut()
	{
		Iterator<MeCabPosTreeNode> i = this.getNodeList().iterator();
		
		while ( i.hasNext() )
		{
			MeCabPosTreeNode node = i.next();
			
			System.out.println("node: " + node.getPosName() + ", low-high: " + node.getLow() + "-" + node.getHigh() + ", \t posID: " + node.getPosID() + "\t parent: " +
					(node.getParentNode() != null ? node.getParentNode().getPosName() + " (posID: " + node.getParentNode().getPosID() + ")" : "null")
					);
		}
		
	}



}
