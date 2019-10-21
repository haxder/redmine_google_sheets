package Redmine.redmineapi.bean;

import Redmine.redmineapi.RedmineException;
import Redmine.redmineapi.internal.Transport;

public class IssueRelation implements Identifiable, FluentStyle {
    private Transport transport;

    public enum TYPE {
        precedes
    }

    /*
     GET /relations/1819.xml
     Response:

     <?xml version="1.0" encoding="UTF-8"?>
     <relation>
       <id>1819</id>
       <issue_id>8470</issue_id>
       <issue_to_id>8469</issue_to_id>
       <relation_type>relates</relation_type>
       <delay/>
     </relation>
     */

    private final PropertyStorage storage = new PropertyStorage();

    /**
     * database numeric Id
     */
    public final static Redmine.redmineapi.bean.Property<Integer> DATABASE_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "id");
    public final static Redmine.redmineapi.bean.Property<Integer> ISSUE_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "issueId");
    public final static Redmine.redmineapi.bean.Property<Integer> ISSUE_TO_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "issueToId");
    public final static Redmine.redmineapi.bean.Property<String> RELATION_TYPE = new Redmine.redmineapi.bean.Property<>(String.class, "relationType");
    public final static Redmine.redmineapi.bean.Property<Integer> DELAY = new Property<>(Integer.class, "delay");

    public IssueRelation(Transport transport) {
        setTransport(transport);
    }

    public IssueRelation(Transport transport, Integer issueId, Integer issueToId, String type) {
        setTransport(transport);
        setIssueId(issueId);
        setIssueToId(issueToId);
        setType(type);
    }

    public IssueRelation setId(Integer id) {
        storage.set(DATABASE_ID, id);
        return this;
    }

    @Override
    public Integer getId() {
        return storage.get(DATABASE_ID);
    }

    public Integer getIssueId() {
        return storage.get(ISSUE_ID);
    }

    public IssueRelation setIssueId(Integer issueId) {
        storage.set(ISSUE_ID, issueId);
        return this;
    }

    public Integer getIssueToId() {
        return storage.get(ISSUE_TO_ID);
    }

    public IssueRelation setIssueToId(Integer issueToId) {
        storage.set(ISSUE_TO_ID, issueToId);
        return this;
    }

    public Integer getDelay() {
        return storage.get(DELAY);
    }

    public IssueRelation setDelay(Integer delay) {
        storage.set(DELAY, delay);
        return this;
    }

    public String getType() {
        return storage.get(RELATION_TYPE);
    }

    public IssueRelation setType(String type) {
        storage.set(RELATION_TYPE, type);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueRelation that = (IssueRelation) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "IssueRelation [getId()=" + getId() + ", issueId=" + getIssueId()
                + ", issueToId=" + getIssueToId() + ", type=" + getType() + ", delay="
                + getDelay() + "]";
    }

    public PropertyStorage getStorage() {
        return storage;
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    /**
     * Each relation must have issueId, issueToId and type set.
     */
    public IssueRelation create() throws RedmineException {
        return transport.addChildEntry(Issue.class, getIssueId().toString(), this);
    }

    public void delete() throws RedmineException {
        transport.deleteObject(IssueRelation.class, Integer.toString(getId()));
    }

}
