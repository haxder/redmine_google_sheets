package Redmine.redmineapi.internal.comm;

import Redmine.redmineapi.RedmineException;

/**
 * Composing content handler.
 * 
 * @author maxkar
 * 
 */
final class ComposingHandler<K, I, R> implements Redmine.redmineapi.internal.comm.ContentHandler<K, R> {

	private final Redmine.redmineapi.internal.comm.ContentHandler<I, R> outer;
	private final Redmine.redmineapi.internal.comm.ContentHandler<K, I> inner;

	public ComposingHandler(Redmine.redmineapi.internal.comm.ContentHandler<I, R> outer,
                            ContentHandler<K, I> inner) {
		super();
		this.outer = outer;
		this.inner = inner;
	}

	@Override
	public R processContent(K content) throws RedmineException {
		return outer.processContent(inner.processContent(content));
	}

}
