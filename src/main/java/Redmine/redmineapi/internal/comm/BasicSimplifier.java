package Redmine.redmineapi.internal.comm;

import Redmine.redmineapi.RedmineException;
import org.apache.http.HttpRequest;

/**
 * Basic transport simplifier.
 * 
 * @author maxkar
 * 
 */
final class BasicSimplifier<K, T> implements SimpleCommunicator<K> {
	private final Redmine.redmineapi.internal.comm.ContentHandler<T, K> contentHandler;
	private final Redmine.redmineapi.internal.comm.Communicator<T> peer;

	public BasicSimplifier(ContentHandler<T, K> contentHandler,
			Communicator<T> peer) {
		this.contentHandler = contentHandler;
		this.peer = peer;
	}

	@Override
	public K sendRequest(HttpRequest request) throws RedmineException {
		return peer.sendRequest(request, contentHandler);
	}

}
