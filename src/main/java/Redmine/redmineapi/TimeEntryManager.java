package Redmine.redmineapi;

import Redmine.redmineapi.bean.TimeEntry;
import Redmine.redmineapi.bean.TimeEntryActivity;
import Redmine.redmineapi.internal.DirectObjectsSearcher;
import Redmine.redmineapi.internal.ResultsWrapper;
import Redmine.redmineapi.internal.Transport;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;
import java.util.Map;

/**
 * Class to operate on Time Entry instances.
 * <p>
 * Sample usage:
 * <pre>
 RedmineManager redmineManager = RedmineManagerFactory.createWithUserAuth(redmineURI, login, password);
 redmineManager.getTimeEntryManager();
 * </pre>
 */
public final class TimeEntryManager {
    private final Transport transport;

    TimeEntryManager(Transport transport) {
        this.transport = transport;
    }


    public List<TimeEntry> getTimeEntries() throws Redmine.redmineapi.RedmineException {
        return transport.getObjectsList(TimeEntry.class);
    }

    /**
     * @param id the database Id of the TimeEntry record
     */
    public TimeEntry getTimeEntry(Integer id) throws Redmine.redmineapi.RedmineException {
        return transport.getObject(TimeEntry.class, id);
    }

    public List<TimeEntry> getTimeEntriesForIssue(Integer issueId) throws Redmine.redmineapi.RedmineException {
        return transport.getObjectsList(TimeEntry.class,
                new BasicNameValuePair("issue_id", Integer.toString(issueId)));
    }

    /**
     * Direct method to search for objects using any Redmine REST API parameters you want.
     * <p>Unlike other getXXXObjects() methods in this library, this one does NOT handle paging for you so
     * you have to provide "offset" and "limit" parameters if you want to control paging.
     *
     * <p>Sample usage:
     <pre>
     final Map<String, String> params = new HashMap<String, String>();
     params.put("project_id", projectId);
     params.put("activity_id", activityId);
     final List<TimeEntry> elements = issueManager.getTimeEntries(params);
     </pre>

     * see other possible parameters on Redmine REST doc page:
     * http://www.redmine.org/projects/redmine/wiki/Rest_TimeEntries#Listing-time-entries
     *
     * @param parameters the http parameters key/value pairs to append to the rest api request
     * @return resultsWrapper with raw response from Redmine REST API
     * @throws RedmineAuthenticationException invalid or no API access key is used with the server, which
     *                                 requires authorization. Check the constructor arguments.
     * @throws Redmine.redmineapi.RedmineException
     */
    public ResultsWrapper<TimeEntry> getTimeEntries(Map<String, String> parameters) throws Redmine.redmineapi.RedmineException {
        return DirectObjectsSearcher.getObjectsListNoPaging(transport, parameters, TimeEntry.class);
    }

    @Deprecated
    public TimeEntry createTimeEntry(TimeEntry obj) throws Redmine.redmineapi.RedmineException {
        return obj.create();
    }

    @Deprecated
    public void deleteTimeEntry(Integer id) throws Redmine.redmineapi.RedmineException {
        new TimeEntry(transport).setId(id).delete();
    }

    public List<TimeEntryActivity> getTimeEntryActivities() throws Redmine.redmineapi.RedmineException {
        return transport.getObjectsList(TimeEntryActivity.class);
    }

    @Deprecated
    public void update(TimeEntry obj) throws RedmineException {
        obj.update();
    }
}
