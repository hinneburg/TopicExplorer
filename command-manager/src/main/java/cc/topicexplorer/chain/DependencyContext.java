package cc.topicexplorer.chain;

import java.util.List;

import org.apache.commons.chain.impl.ContextBase;

public class DependencyContext extends ContextBase {

	private static final long serialVersionUID = 5551810374358381131L;
	
	protected List<String> afterDependencies;
	protected List<String> beforeDependencies;
	protected List<String> optionalAfterDependencies;
	protected List<String> optionalBeforeDependencies;
		
	public void setAfterDependencies(List<String> dependencies) {
		afterDependencies = dependencies;
	}

	public List<String> getAfterDependencies() {
		return afterDependencies;
	}
	
	public void setBeforeDependencies(List<String> dependencies) {
		beforeDependencies = dependencies;
	}

	public List<String> getBeforeDependencies() {
		return beforeDependencies;
	}
	
	public void setOptionalAfterDependencies(List<String> dependencies) {
		optionalAfterDependencies = dependencies;
	}

	public List<String> getOptionalAfterDependencies() {
		return optionalAfterDependencies;
	}
	
	public void setOptionalBeforeDependencies(List<String> dependencies) {
		optionalBeforeDependencies = dependencies;
	}

	public List<String> getOptionalBeforeDependencies() {
		return optionalBeforeDependencies;
	}

}
