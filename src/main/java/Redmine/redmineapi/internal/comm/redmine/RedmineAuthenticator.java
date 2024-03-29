package Redmine.redmineapi.internal.comm.redmine;

import Redmine.redmineapi.RedmineException;
import Redmine.redmineapi.RedmineInternalError;
import Redmine.redmineapi.internal.comm.Communicator;
import Redmine.redmineapi.internal.comm.ContentHandler;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;

import java.io.UnsupportedEncodingException;

public class RedmineAuthenticator<K> implements Communicator<K> {
	/**
	 * Header value.
	 */
	private String authKey;

	/**
	 * Used charset.
	 */
	private final String charset;

	/**
	 * Peer communicator.
	 */
	private final Communicator<K> peer;

	public RedmineAuthenticator(Communicator<K> peer, String charset) {
		this.peer = peer;
		this.charset = charset;
	}

	public void setCredentials(String login, String password) {
		if (login == null) {
			authKey = null;
			return;
		}
		try {
			authKey = "Basic "
					+ Base64.encodeBase64String(
							(login + ':' + password).getBytes(charset)).trim();
		} catch (UnsupportedEncodingException e) {
			throw new RedmineInternalError(e);
		}
	}

	@Override
	public <R> R sendRequest(HttpRequest request, ContentHandler<K, R> handler)
			throws RedmineException {
		if (authKey != null)
			request.addHeader("Authorization", authKey);
		return peer.sendRequest(request, handler);
	}

}
