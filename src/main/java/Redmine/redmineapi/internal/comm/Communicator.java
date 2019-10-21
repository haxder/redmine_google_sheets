package Redmine.redmineapi.internal.comm;

import Redmine.redmineapi.RedmineException;
import org.apache.http.HttpRequest;

public interface Communicator<K> {

	/**
	 * Performs a request.
	 * 
	 * @return the response body.
	 */
	<R> R sendRequest(HttpRequest request, ContentHandler<K, R> contentHandler) throws RedmineException;

}