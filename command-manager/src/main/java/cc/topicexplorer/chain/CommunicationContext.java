package cc.topicexplorer.chain;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.impl.ContextBase;

public class CommunicationContext extends ContextBase {

	private static final long serialVersionUID = -7187427960916517256L;
	private Map<String, Object> objects = new HashMap<String, Object>();

	public void put(String name, Object object) {
		objects.put(name, object);
	}

	public Object get(String name) {
		return objects.get(name);
	}

	public void remove(String name) {
		objects.remove(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(@SuppressWarnings("rawtypes") Map map) {
		objects.putAll(map);
	}

	public CommunicationContext clone() {
		CommunicationContext communicationContext = new CommunicationContext();

		communicationContext.putAll(objects);

		return communicationContext;
	}
}
