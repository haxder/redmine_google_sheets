package Redmine.redmineapi.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PropertyStorage {
    private final Map<Redmine.redmineapi.bean.Property<?>, Object> map = new HashMap<>();

    public final <T> T get(Redmine.redmineapi.bean.Property<T> prop) {
        return prop.getType().cast(map.get(prop));
    }

    final <T> void set(Redmine.redmineapi.bean.Property<T> prop, T value) {
        map.put(prop, value);
    }

    public final boolean isPropertySet(Redmine.redmineapi.bean.Property<?> property) {
        return map.containsKey(property);
    }

    public Set<Map.Entry<Property<?>, Object>> getProperties() {
        return map.entrySet();
    }
}
