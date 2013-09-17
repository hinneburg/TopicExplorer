//package cc.topicexplorer.chain;
//
//import org.jooq.impl.Factory;
//
//public class DatabaseContext extends PropertyContext {
//
//	/**
//     * 
//     */
//	private static final long serialVersionUID = 5551810374358381131L;
//
//	public enum ChainState {
//		CREATE, FILL
//	}
//
//	private ChainState chainState;
//	private cc.topicexplorer.database.Database database;
//	private Factory create;
//	
//	public void setDatabase(cc.topicexplorer.database.Database database) {
//		this.database = database;
//	}
//
//	public cc.topicexplorer.database.Database getDatabase() {
//		return database;
//	}
//
//	public void setCreate(Factory create) {
//		this.create = create;
//	}
//
//	public Factory getCreate() {
//		return create;
//	}
//	public void setChainState(ChainState chainState) {
//		this.chainState = chainState;
//	}
//
//	public ChainState getChainState() {
//		return chainState;
//	}
//
//}
