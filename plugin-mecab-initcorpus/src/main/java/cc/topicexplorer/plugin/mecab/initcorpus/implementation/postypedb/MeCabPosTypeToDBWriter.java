package cc.topicexplorer.plugin.mecab.initcorpus.implementation.postypedb;

import java.sql.SQLException;
import java.util.Iterator;

import cc.topicexplorer.database.Database;



public class MeCabPosTypeToDBWriter
{
	
	protected cc.topicexplorer.database.Database database;
	private MeCabPosTree mecabPosTypeTree;


	public MeCabPosTypeToDBWriter( Database database )
	{
		this.database = database;
		
		initDBConnection();
	}

	
	private void buildMeCabPosTypeData()
	{
		MeCabPosTreeBuilder tb = new MeCabPosTreeBuilder();

		mecabPosTypeTree = tb.parsePosIDs();
	}
	
	

	private void initDBConnection() {
		try {
//			dbManager = new MySQLManager( DBConfig.host, DBConfig.db, DBConfig.user, DBConfig.password );
			
			// TODO init DB 
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	
	
	public void writePosTypesToDB() throws SQLException
	{
//		if ( !database.isInitialized() )
//			return;
		
		this.buildMeCabPosTypeData();
		
		
		Iterator<MeCabPosTreeNode> i = mecabPosTypeTree.getNodeList().iterator();
		
		
		while ( i.hasNext() )
		{
			MeCabPosTreeNode node = i.next();
			
			// skip root node
			if ( node.getPosName().equals("root") )
				continue;
			
			String query = this.buildInsertQueryString(node);
			
			// write to DB
			database.executeQuery(query);
		}
	}
	
	
	private String buildInsertQueryString( MeCabPosTreeNode node )
	{
		int posID = node.getParentNode().getPosID();
		String strPosID = ( posID < 0 ) ? "NULL" : "'" + String.valueOf(posID) + "'";
		
		String query = "INSERT INTO POS_TYPE (POS, LOW, HIGH, DESCRIPTION, PARENT_POS) " +
				"VALUES ('" + node.getPosID() + "', '" + node.getLow() +
					"', '" + node.getHigh() + "', '" + node.getPosName() +
					"', " + strPosID + ")";

		return query;
	}

}
