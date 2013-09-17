package cc.topicexplorer.chain.commands;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.*;

import cc.topicexplorer.chain.DependencyContext;

/**
 * This class extends a commands functionality by adding dependencies. Every
 * command extending this class needs to implement a specialExecute, which will
 * act as the normal execute of a org.apache.commons.chain.Command.
 * <p>
 * If the execute is invoked with a DependencyContext the addDependencies method
 * is called and the dependencies are set, otherwise the specialExecute is
 * invoked.
 * <p>
 * If a command has dependencies to other commands, those can be specified in
 * the afterDependencies, beforeDependencies, optionalAfterDependencies and
 * optionalBeforeDependencies variables. For doing this the addDependencies
 * method should be overwritten. Then a sample addDependencies could contain the
 * following lines:
 * <p>
 * beforeDependencies.add("A"); afterDependencies.add("C");
 * <p>
 * This means that the actual command needs command A to be executed, and
 * command C needs the actual command to be executed before itself.
 * 
 * @author Sebastian Baer
 * 
 */

public abstract class DependencyCommand implements Command {

	protected List<String> afterDependencies = new ArrayList<String>();
	protected List<String> beforeDependencies = new ArrayList<String>();
	protected List<String> optionalAfterDependencies = new ArrayList<String>();
	protected List<String> optionalBeforeDependencies = new ArrayList<String>();
	protected Logger logger = Logger.getRootLogger();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.chain.Command#execute(org.apache.commons.chain.Context
	 * )
	 */
	@Override
	public boolean execute(Context context) throws Exception {

		logger.info("Current Command : [ " + getClass() + " ]");
		
		if (DependencyContext.class.isInstance(context)) {
			DependencyContext dependencyContext = (DependencyContext) context;
			addDependencies();
			dependencyContext.setAfterDependencies(afterDependencies);
			dependencyContext.setBeforeDependencies(beforeDependencies);
			dependencyContext
					.setOptionalAfterDependencies(optionalAfterDependencies);
			dependencyContext
					.setOptionalBeforeDependencies(optionalBeforeDependencies);
		} else {
			specialExecute(context);
		}

		return false;
	}

	public void addDependencies() {
	};

	public abstract void specialExecute(Context context) throws Exception;

}
