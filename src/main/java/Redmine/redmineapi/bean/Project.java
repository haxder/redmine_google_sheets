package Redmine.redmineapi.bean;

import Redmine.redmineapi.NotFoundException;
import Redmine.redmineapi.RedmineAuthenticationException;
import Redmine.redmineapi.RedmineException;
import Redmine.redmineapi.internal.Transport;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Redmine Project.
 */
public class Project implements Identifiable, Serializable, FluentStyle {
	private static final long serialVersionUID = 4529305420978716446L;

    private final PropertyStorage storage = new PropertyStorage();

    /**
     * database ID
     */
    public final static Property<Integer> DATABASE_ID = new Property<>(Integer.class, "id");

    /**
     * String "identifier" (human-readable name without spaces and other extra stuff)
     */
    public final static Property<String> STRING_IDENTIFIER = new Property<>(String.class, "identifier");

    /**
     * Can contain any symbols
     */
    public final static Property<String> NAME = new Property<>(String.class, "name");
    public final static Property<String> DESCRIPTION = new Property<>(String.class, "description");
    public final static Property<String> HOMEPAGE = new Property<>(String.class, "homepage");
    public final static Property<Date> CREATED_ON = new Property<>(Date.class, "createdOn");
    public final static Property<Date> UPDATED_ON = new Property<>(Date.class, "updatedOn");

    /**
     * This is the *database ID*, not a String-based key.
     */
    public final static Property<Integer> PARENT_DATABASE_ID = new Property<>(Integer.class, "parentId");
    public final static Property<Boolean> PUBLIC = new Property<>(Boolean.class, "public");
    public final static Property<Boolean> INHERIT_MEMBERS = new Property<>(Boolean.class, "inheritMembers");
    public final static Property<Set<CustomField>> CUSTOM_FIELDS = (Property<Set<CustomField>>) new Property(Set.class, "customFields");

    /**
     * Trackers available for this project
     */
    public final static Property<Set<Tracker>> TRACKERS = (Property<Set<Tracker>>) new Property(Set.class, "trackers");
    private Transport transport;

    public Project(Transport transport) {
        this.transport = transport;
        storage.set(CUSTOM_FIELDS, new HashSet<>());
        storage.set(TRACKERS, new HashSet<>());
    }

    public Project(Transport transport, String name, String key) {
        this(transport);
        setName(name);
        setIdentifier(key);
    }

    public Project setId(Integer id) {
        storage.set(DATABASE_ID, id);
        return this;
    }

    public String getHomepage() {
        return storage.get(HOMEPAGE);
    }

    public Project setHomepage(String homepage) {
        storage.set(HOMEPAGE, homepage);
        return this;
    }

    /**
     * @return project's string "key" (not a numeric database id!). Example: "project_ABC"
     */
    public String getIdentifier() {
        return storage.get(STRING_IDENTIFIER);
    }

    public Project setIdentifier(String identifier) {
        storage.set(STRING_IDENTIFIER, identifier);
        return this;
    }

    /**
     * @return numeric database ID
     */
    @Override
    public Integer getId() {
        return storage.get(DATABASE_ID);
    }

    /**
     * @return project name
     */
    public String getName() {
        return storage.get(NAME);
    }

    /**
     * @param name the project name
     */
    public Project setName(String name) {
        storage.set(NAME, name);
        return this;
    }

    /**
     * @return Trackers allowed in this project (e.g.: Bug, Feature, Support, Task, ...)
     */
    public Collection<Tracker> getTrackers() {
        return Collections.unmodifiableCollection(storage.get(TRACKERS));
    }

    public void addTrackers(Collection<Tracker> trackers) {
        storage.get(TRACKERS).addAll(trackers);
    }

    public Tracker getTrackerByName(String trackerName) {
        for (Tracker t : getTrackers()) {
            if (t.getName().equals(trackerName)) return t;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + getId() +
                ", identifier='" + getIdentifier() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }

    public String getDescription() {
        return storage.get(DESCRIPTION);
    }

    public Project setDescription(String description) {
        storage.set(DESCRIPTION, description);
        return this;
    }

    public Date getCreatedOn() {
        return storage.get(CREATED_ON);
    }

    public Project setCreatedOn(Date createdOn) {
        storage.set(CREATED_ON, createdOn);
        return this;
    }

    public Date getUpdatedOn() {
        return storage.get(UPDATED_ON);
    }

    public Project setUpdatedOn(Date updatedOn) {
        storage.set(UPDATED_ON, updatedOn);
        return this;
    }

    /**
     * Redmine's REST API "get project" operation does NOT return the 
     * parent project ID in redmine 1.1.2 (and maybe earlier). Which means 
     * calling getParentId() of the project loaded from Redmine server will
     * return <strong>NULL</strong> with that redmine. This bug was fixed in redmine 1.2.1.
     * See bug http://www.redmine.org/issues/8229
     * 
     *
     * @return the parent project Id if it was set programmatically or NULL (!!!) if the project was loaded from the server.
     */
    public Integer getParentId() {
        return storage.get(PARENT_DATABASE_ID);
    }

    public Project setParentId(Integer parentId) {
        storage.set(PARENT_DATABASE_ID, parentId);
        return this;
    }

    /**
     *
     * @return true if the project is public, false if the project is private. 
     * Returns <code>null</code> if the project visibility was not specified or if the project was just retrieved from server.
     *
     * @since Redmine 2.6.0. see http://www.redmine.org/issues/17628 . this property is for writing only before Redmine 2.6.0.
     * The value is not returned by older Redmine versions.
     */
    @Deprecated
    public Boolean getProjectPublic() {
        return storage.get(PUBLIC);
    }
    
    public Project setInheritMembers(Boolean inheritMembers) {
        storage.set(INHERIT_MEMBERS, inheritMembers);
        return this;
    }

    public Boolean getInheritMembers() {
        return storage.get(INHERIT_MEMBERS);
    }

    public Project setProjectPublic(Boolean projectPublic) {
        storage.set(PUBLIC, projectPublic);
        return this;
    }

    public Collection<CustomField> getCustomFields() {
        return storage.get(CUSTOM_FIELDS);
    }

    public Project addCustomFields(Collection<CustomField> customFields) {
        storage.get(CUSTOM_FIELDS).addAll(customFields);
        return this;
    }

    public CustomField getCustomFieldById(int customFieldId) {
        for (CustomField customField : getCustomFields()) {
            if (customFieldId == customField.getId()) {
                return customField;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (getId() != null ? !getId().equals(project.getId()) : project.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public PropertyStorage getStorage() {
        return storage;
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
        PropertyStorageUtil.updateCollections(storage, transport);
    }

    /**
     * Sample usage:
     *
     * <pre>
     * {@code
     * 	Project project = new Project(transport);
     * 	Long timeStamp = Calendar.getInstance().getTimeInMillis();
     * 	String key = "projkey" + timeStamp;
     * 	String name = &quot;project number &quot; + timeStamp;
     * 	String description = &quot;some description for the project&quot;;
     * 	project.setIdentifier(key)
     * 	  .setName(name)
     * 	  .setDescription(description)
     * 	  .create();
     * }
     * </pre>
     *
     * @return the newly created Project object.
     * @throws RedmineAuthenticationException invalid or no API access key is used with the server, which
     *                                 requires authorization. Check the constructor arguments.
     * @throws RedmineException
     */
    public Project create() throws RedmineException {
        return transport.addObject(this, new BasicNameValuePair("include",
                "trackers"));
    }

    public void update() throws RedmineException {
        transport.updateObject(this);
    }

    /**
     * The project object must have projectKey set (String-based identifier like "project-ABC",
     * NOT a database numeric ID!)
     *
     * @throws RedmineAuthenticationException invalid or no API access key is used with the server, which
     *                                 requires authorization. Check the constructor arguments.
     * @throws NotFoundException       if the project with this key is not found
     */
    public void delete() throws RedmineException {
        transport.deleteObject(Project.class, getIdentifier());
    }
}
