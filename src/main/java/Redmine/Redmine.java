package Redmine;

import Redmine.redmineapi.*;
import Redmine.redmineapi.bean.Issue;

import java.util.*;

public class Redmine {
    private static String API_KEY = "d821ff33a0c1f69b9b901564948f71cefe276f6c";
    private static String REDMINE_URL = "https://www.ios-developer001.com/redmine";
    private static List<String> IGNORE_LIST = Arrays.asList("【ＱＡ内容】", "【QA内容】");
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
        List<Issue> results = new ArrayList<>();
        Params params = new Params()
                .add("assigned_to_id", "59")
                .add("limit", "1000")
                .add("status_id","o");
        try {
            results = issueManager.getIssues(params).getResults();
            Iterator itr = results.iterator();
            while (itr.hasNext()) {
                Issue temp = (Issue) itr.next();
                if (temp.getDescription().contains("【ＱＡ内容】") || temp.getDescription().contains("【QA内容】")) {
                    itr.remove();
                }
            }
            System.out.println("task:" + results.size());
            return results;
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return null;
    }
}
