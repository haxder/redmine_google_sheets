package Redmine.redmineapi.internal.comm;

import Redmine.redmineapi.RedmineException;
import org.apache.http.HttpRequest;

/**
 * FMap communicator.
 * 
 * @author maxkar
 * 
 */
final class FmapCommunicator<K, I> implements Redmine.redmineapi.internal.comm.Communicator<K> {

	private final Redmine.redmineapi.internal.comm.ContentHandler<I, K> handler;
	private final Redmine.redmineapi.internal.comm.Communicator<I> peer;

	public FmapCommunicator(Redmine.redmineapi.internal.comm.ContentHandler<I, K> handler, Communicator<I> peer) {
		super();
		this.handler = handler;
		this.peer = peer;
	}

	@Override
	public <R> R sendRequest(HttpRequest request,
			ContentHandler<K, R> contentHandler) throws RedmineException {
		return peer.sendRequest(request,
				Communicators.compose(contentHandler, handler));
	}

}
