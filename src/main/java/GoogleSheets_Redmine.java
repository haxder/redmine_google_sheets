import Redmine.Redmine;
import Redmine.redmineapi.bean.Issue;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheets_Redmine {
    private static final String APPLICATION_NAME = "Google Sheets API";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String spreadsheetId = "1fmtom4SeEz96Okd-6Nl65UV0KJZZoTDiWowvfcEpPrI";
    //private static final String spreadsheetId = "1PtqWCVqGnhobK_CE4EVoU9o_TheVzCe5vXOLUcOa51o";
    private static final String range = "Luvina-Task!B2:J";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final String IOS_URL= "https://www.ios-developer001.com/redmine/issues";
    private static List<Integer> google_sheet_ticket_ids = new ArrayList<Integer>();


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheets_Redmine.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Sheets createSheetsService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static ValueRange getDataRange() throws IOException, GeneralSecurityException {
        Sheets service = createSheetsService();
        return service.spreadsheets().values()
                .get(GoogleSheets_Redmine.spreadsheetId, GoogleSheets_Redmine.range)
                .execute();
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {

        ValueRange response = getDataRange();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
            return;
        } else {
            System.out.println("---------------------------Start---------------------------");
            int index = 1;
            for (List<Object> row : values) {
                index++;
                System.out.println(index);
                if (row.size() == 0 || "".equals(row.get(0)) || "終了".equals(row.get(3))) {
                    continue;
                }
                // Print columns A and E, which correspond to indices 0 and 4.
                Integer issueID = Integer.valueOf(row.get(0).toString());
                Issue issue = Redmine.getIssue(issueID);
                google_sheet_ticket_ids.add(issueID);


                try {
                    updateRow(row, issue);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
                System.out.printf("---Xử lý xong ID:%s-----\n", row.get(0));
            }

            System.out.println("---------------------------End---------------------------");
        }

        batchUpdateValues("USER_ENTERED", values);
    }

    private static void updateRow(List<Object> row, Issue issue) {
        if (issue == null) {
            return;
        }
        // 0 - ID Redmine
        // 1 - ID Lupack
        // 2 - Độ ưu tiên
        row.set(2, issue.getPriorityText());
        // 3 - Ticket status
        row.set(3, issue.getStatusName());
        // 4 - 担当者
        row.set(4, issue.getAssigneeName());
        // 5 - 区分
        row.set(5, issue.getCustomFieldValuesById(148));
        // 8 - Title
        if(!"".equals(issue.getSubject())) {
            row.set(8, issue.getSubject());
        }
    }

    private static BatchUpdateValuesResponse batchUpdateValues(String valueInputOption,
                                                               List<List<Object>> _values) throws GeneralSecurityException, IOException {
        Sheets service = createSheetsService();
        List<List<Object>> values;

        values = _values;
        List<List<Object>> ticketNew = getTicketNew();
        if (ticketNew.size() > 0) {
            values.addAll(ticketNew);
        }
        List<ValueRange> data = new ArrayList<ValueRange>();
        data.add(new ValueRange()
                .setRange(GoogleSheets_Redmine.range)
                .setValues(values));

        BatchUpdateValuesRequest body = new BatchUpdateValuesRequest()
                .setValueInputOption(valueInputOption)
                .setData(data);
        BatchUpdateValuesResponse result =
                service.spreadsheets().values().batchUpdate(GoogleSheets_Redmine.spreadsheetId, body).execute();
        System.out.printf("%d cells updated.", result.getTotalUpdatedCells());
        return result;
    }

    private static List<List<Object>> getTicketNew() {
        List<List<Object>> results = new ArrayList<List<Object>>();
        List<Issue> issues = Redmine.getIssuesAssgineME();
        if(issues == null) {
            return results;
        }
        for (Issue is : issues) {
            if (google_sheet_ticket_ids.contains(is.getId())) {
                continue;
            }
            // ticket moi
            List<Object> objects = Arrays.<Object>asList(is.getId(), new String(""),is
                    .getPriorityText(), is.getStatusName(), is.getAssigneeName());
            results.add(objects);
        }
        return results;
    }
}
