package Redmine.redmineapi.bean;

import Redmine.redmineapi.internal.Transport;

import java.util.Collection;

public class TransportDecorator {
    static void decorate(Collection<?> collection, Transport transport) {
        collection.forEach(e -> {
            if (e instanceof FluentStyle) {
                ((FluentStyle) e).setTransport(transport);
            }
        });
    }
}
