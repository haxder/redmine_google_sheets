package Redmine.redmineapi.internal.comm.redmine;

import Redmine.redmineapi.*;
import Redmine.redmineapi.internal.RedmineJSONParser;
import Redmine.redmineapi.internal.comm.BasicHttpResponse;
import Redmine.redmineapi.internal.comm.Communicators;
import Redmine.redmineapi.internal.comm.ContentHandler;
import org.apache.http.HttpStatus;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RedmineErrorHandler implements
        ContentHandler<BasicHttpResponse, BasicHttpResponse> {

	private static final Map<String, String> ERROR_REMAP = new HashMap<>();

	static {
		ERROR_REMAP
				.put("Priority can't be blank",
						"Priority can't be blank. No default priority is set in the Redmine server settings. please use menu \"Administration -> Enumerations -> Issue Priorities\" to set the default priority.");
	}

	@Override
	public BasicHttpResponse processContent(BasicHttpResponse httpResponse)
			throws RedmineException {
		final int responseCode = httpResponse.getResponseCode();
		if (responseCode == HttpStatus.SC_UNAUTHORIZED) {
			throw new RedmineAuthenticationException(
					"Authorization error. Please check if you provided a valid API access key or Login and Password and REST API service is enabled on the server.");
		}
		if (responseCode == HttpStatus.SC_FORBIDDEN) {
			throw new NotAuthorizedException(
					"Forbidden. Please check the user has proper permissions.");
		}
		if (responseCode == HttpStatus.SC_NOT_FOUND) {
			throw new NotFoundException(
					"Server returned '404 not found'. response body:"
							+ getContent(httpResponse));
		}

		if (responseCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
			try {
				final List<String> sourceErrors = RedmineJSONParser.parseErrors(getContent(httpResponse));
				final List<String> errors = sourceErrors.stream()
						.map(this::remap)
						.collect(Collectors.toList());
				throw new RedmineProcessingException(errors);
			} catch (JSONException e) {
				throw new RedmineFormatException("Bad redmine error response", e);
			}
		}
		return httpResponse;
	}

	private String remap(String message) {
		final String guess = ERROR_REMAP.get(message);
		return guess != null ? guess : message;
	}

	private String getContent(BasicHttpResponse entity) throws RedmineException {
		return Communicators.contentReader().processContent(entity);
	}

}
