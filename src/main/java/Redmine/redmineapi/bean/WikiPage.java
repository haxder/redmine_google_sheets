package Redmine.redmineapi.bean;

import java.util.Date;

public class WikiPage {
    private final PropertyStorage storage = new PropertyStorage();

    public final static Redmine.redmineapi.bean.Property<String> TITLE = new Redmine.redmineapi.bean.Property<>(String.class, "title");
    public final static Redmine.redmineapi.bean.Property<Integer> VERSION = new Redmine.redmineapi.bean.Property<>(Integer.class, "version");
    public final static Redmine.redmineapi.bean.Property<Date> CREATED_ON = new Redmine.redmineapi.bean.Property<>(Date.class, "createdOn");
    public final static Redmine.redmineapi.bean.Property<Date> UPDATED_ON = new Property<>(Date.class, "updatedOn");

    public String getTitle() {
        return storage.get(TITLE);
    }

    public WikiPage setTitle(String title) {
        storage.set(TITLE, title);
        return this;
    }

    public Integer getVersion() {
        return storage.get(VERSION);
    }

    public WikiPage setVersion(Integer version) {
        storage.set(VERSION, version);
        return this;
    }

    public Date getCreatedOn() {
        return storage.get(CREATED_ON);
    }

    public WikiPage setCreatedOn(Date createdOn) {
        storage.set(CREATED_ON, createdOn);
        return this;
    }

    public Date getUpdatedOn() {
        return storage.get(UPDATED_ON);
    }

    public WikiPage setUpdatedOn(Date updatedOn) {
        storage.set(UPDATED_ON, updatedOn);
        return this;
    }

    @Override
    public String toString() {
        return "WikiPage{" +
                "title='" + getTitle() + '\'' +
                ", version=" + getVersion() +
                ", createdOn=" + getCreatedOn() +
                ", updatedOn=" + getUpdatedOn() +
                '}';
    }
}