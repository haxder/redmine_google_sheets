package Redmine.redmineapi.internal.comm;

import Redmine.redmineapi.RedmineException;
import org.apache.http.HttpRequest;

public interface SimpleCommunicator<T> {
	/**
	 * Performs a request.
	 * 
	 * @return the response body.
	 */
	T sendRequest(HttpRequest request) throws RedmineException;

}
