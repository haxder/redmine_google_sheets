package Redmine.redmineapi.bean;

import java.util.Date;
import java.util.List;

public class JournalFactory {
    public static Redmine.redmineapi.bean.Journal create(Integer id, String notes, Redmine.redmineapi.bean.User user, Date createdOn) {
        Redmine.redmineapi.bean.Journal journal = new Redmine.redmineapi.bean.Journal().setId(id);
        journal.setNotes(notes);
        journal.setUser(user);
        journal.setCreatedOn(createdOn);
        return journal;
    }

    public static Redmine.redmineapi.bean.Journal create(Integer id, String notes, User user, Date createdOn, List<JournalDetail> details) {
        Redmine.redmineapi.bean.Journal journal = new Journal().setId(id);
        journal.setNotes(notes);
        journal.setUser(user);
        journal.setCreatedOn(createdOn);
        journal.addDetails(details);
        return journal;
    }
}
