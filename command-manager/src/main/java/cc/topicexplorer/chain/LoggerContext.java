package cc.topicexplorer.chain;

import org.apache.commons.chain.impl.ContextBase;

import org.apache.log4j.Logger;

public class LoggerContext extends ContextBase {
	private static final long serialVersionUID = 5551810374358381131L;

	protected Logger logger;
	
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Logger getLogger() {
		return logger;
	}
}