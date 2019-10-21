package Redmine.redmineapi.bean;

import Redmine.redmineapi.RedmineException;
import Redmine.redmineapi.internal.Transport;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User or group membership.
 */
public class Membership implements Identifiable, FluentStyle {

	private final PropertyStorage storage= new PropertyStorage();

	/**
	 * database numeric ID.
	 */
	public final static Redmine.redmineapi.bean.Property<Integer> DATABASE_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "id");
	public final static Redmine.redmineapi.bean.Property<Redmine.redmineapi.bean.Project> PROJECT = new Redmine.redmineapi.bean.Property<>(Redmine.redmineapi.bean.Project.class, "project");
	/**
	 * User. Not set for "group" membership.
	 */
	public final static Redmine.redmineapi.bean.Property<Integer> USER_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "userId");
        public final static Redmine.redmineapi.bean.Property<String> USER_NAME = new Redmine.redmineapi.bean.Property<>(String.class, "userName");
        /**
         * Group. Not set for "user" membership.
         */
	public final static Redmine.redmineapi.bean.Property<Integer> GROUP_ID = new Redmine.redmineapi.bean.Property<>(Integer.class, "groupId");
        public final static Redmine.redmineapi.bean.Property<String> GROUP_NAME = new Redmine.redmineapi.bean.Property<>(String.class, "groupName");
	public final static Redmine.redmineapi.bean.Property<Set<Redmine.redmineapi.bean.Role>> ROLES = (Redmine.redmineapi.bean.Property<Set<Redmine.redmineapi.bean.Role>>) new Property(Set.class, "roles");
	private Transport transport;

	public Membership(Transport transport) {
		storage.set(ROLES, new HashSet<>());
		setTransport(transport);
	}

	public Membership(Transport transport, Redmine.redmineapi.bean.Project project, int userId) {
		this(transport);
		setProject(project);
		setUserId(userId);
	}

    /**
     * @param id database ID.
     */
    public Membership setId(Integer id) {
        storage.set(DATABASE_ID, id);
        return this;
    }

    @Override
	public Integer getId() {
		return storage.get(DATABASE_ID);
	}

	public Redmine.redmineapi.bean.Project getProject() {
		return storage.get(PROJECT);
	}

	public Membership setProject(Redmine.redmineapi.bean.Project project) {
		storage.set(PROJECT, project);
		return this;
	}

	public Integer getUserId() {
		return storage.get(USER_ID);
	}

	public Membership setUserId(Integer id) {
		storage.set(USER_ID, id);
		return this;
	}

    public Integer getGroupId() {
        return storage.get(GROUP_ID);
    }

    public void setGroupId(Integer id) {
		storage.set(GROUP_ID, id);
    }
    
	public String getUserName() {
		return storage.get(USER_NAME);
	}

	public void setUserName(String id) {
		storage.set(USER_NAME, id);
	}

    public String getGroupName() {
        return storage.get(GROUP_NAME);
    }

    public void setGroupName(String id) {
		storage.set(GROUP_NAME, id);
    }

    public Collection<Redmine.redmineapi.bean.Role> getRoles() {
		return Collections.unmodifiableCollection(storage.get(ROLES));
	}

	public Membership addRoles(Collection<Role> roles) {
		storage.get(ROLES).addAll(roles);
		return this;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Membership that = (Membership) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
	public String toString() {
		return "Membership [id=" + getId() + ", project=" + getProject() + ", userId="
				+ getUserId() + ", groupId=" + getGroupId() + ", roles=" + getRoles() + "]";
	}

	public PropertyStorage getStorage() {
		return storage;
	}

	/**
	 * Required attributes: 1) project    2) either userId/groupId or roles collection
	 */
	public Membership create() throws RedmineException {
		if (getProject() == null) {
			throw new IllegalArgumentException("Project must be set");
		}
		if (getUserId() == null && getRoles().isEmpty()) {
			throw new IllegalArgumentException("Either User or Roles field must be set");
		}
		return transport.addChildEntry(Project.class, getProject().getId() + "", this);
	}

	public void update() throws RedmineException {
		transport.updateObject(this);
	}

	/**
	 * this object must have ID property set
	 */
	public void delete() throws RedmineException {
		transport.deleteObject(Membership.class, Integer.toString(getId()));
	}

	@Override
	public void setTransport(Transport transport) {
		this.transport = transport;
		PropertyStorageUtil.updateCollections(storage, transport);
	}
}
