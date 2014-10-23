package cc.topicexplorer.plugin.pos.preprocessing.dispatchers;

import cc.topicexplorer.plugin.pos.preprocessing.implementation.postagger.JPOSMeCab;

public class meCabDispatcher extends Dispatcher {

	public meCabDispatcher(int language) {
		super(language);
		// TODO Auto-generated constructor stub
	}
	
	private JPOSMeCab meCab;
	
	@Override
	public void initialize(String path)
	{
		meCab = new JPOSMeCab(path);
	}

}
