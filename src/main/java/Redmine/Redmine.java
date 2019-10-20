package Redmine;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Redmine {
    private static String API_KEY = "d821ff33a0c1f69b9b901564948f71cefe276f6c";
    private static String REDMINE_URL = "https://www.ios-developer001.com/redmine";

    private static IssueManager issueManager = createIssueManager();

    private static IssueManager createIssueManager() {
        if (issueManager != null) {
            return issueManager;
        }
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(REDMINE_URL, API_KEY);
        issueManager = mgr.getIssueManager();
        return issueManager;
    }

    public static Issue getIssue(Integer issueID) {
        Issue issue;
        try {
            issue = issueManager.getIssueById(issueID);
        } catch (RedmineException e) {
            return null; // khong tim thay ID redmine
        }
        return issue;
    }

    public static List<Issue> getIssuesAssgineME() {
        createIssueManager();
        Map<String, String> param = new HashMap<String, String>();
        param.put("assigned_to_id", "59");
        try {
            return issueManager.getIssues(param).getResults();
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return null;
    }
}
