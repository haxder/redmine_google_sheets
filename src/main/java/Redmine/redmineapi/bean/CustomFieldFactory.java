package Redmine.redmineapi.bean;

import Redmine.redmineapi.bean.CustomField;

public class CustomFieldFactory {

    public static Redmine.redmineapi.bean.CustomField create(Integer id, String name, String value) {
        Redmine.redmineapi.bean.CustomField field = new CustomField().setId(id);
        field.setName(name);
        field.setValue(value);
        return field;
    }
}
