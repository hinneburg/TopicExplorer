package cc.topicexplorer.web;

import cc.commandmanager.core.Context;

public class WebChainManagement {
	private static boolean isInitialized = false;
	private static Context context;

	private WebChainManagement() {
		throw new UnsupportedOperationException();
	}

	public static void init(Context context) {
		if (!isInitialized) {
			WebChainManagement.context = context;
			isInitialized = true;
		} else {
			throw new IllegalStateException("Class has already been initialized.");
		}
	}

	public static Context getContext() {
		if (isInitialized) {
			return context;
		} else {
			throw new IllegalStateException("Class must be initialized before getContext can be called.");
		}
	}

}
