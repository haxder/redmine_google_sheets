import Redmine.Redmine;
import Redmine.redmineapi.bean.Issue;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import common.DateUtils;
import common.GoogleSheetCommonUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheets_Redmine {
    private static final String spreadsheetId = "1fmtom4SeEz96Okd-6Nl65UV0KJZZoTDiWowvfcEpPrI";
    //private static final String spreadsheetId = "1PtqWCVqGnhobK_CE4EVoU9o_TheVzCe5vXOLUcOa51o";
    private static final String range = "Luvina_Task!B2:K";
    private static List<Integer> google_sheet_ticket_ids = new ArrayList<Integer>();

    private static ValueRange getDataRange() throws IOException, GeneralSecurityException {
        Sheets service = GoogleSheetCommonUtils.getSheetsService();
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

                if (row.size() == 0 || "".equals(row.get(0)) || "終了".equals(getDataOfRow(row, 3))) {
                    continue;
                }
                // Print columns A and E, which correspond to indices 0 and 4.
                Integer issueID = Integer.valueOf(row.get(0).toString());
                Issue issue = Redmine.getIssue(issueID);
                google_sheet_ticket_ids.add(issueID);


                try {
                    updateRow(row, issue);
                } catch (Exception e) {
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
        updateRow(row, 2, issue.getPriorityText());

        // 3 - Ticket status
        updateRow(row, 3, issue.getStatusName());

        // 4 - 担当者
        updateRow(row, 4, issue.getAssigneeName());

        // 5 - 区分
        updateRow(row, 5, issue.getCustomFieldValuesById(148));
        // 6 - Chức năng
        updateRow(row, 6, issue.getCustomFieldValuesById(179));
        // 7 - subject
        updateRow(row, 7, issue.getSubject());
        // 8 - công số dự kiến
        updateRow(row, 8, issue.getCustomFieldValuesById(204).replace(".", ","));
        //9  - deadline
        if (issue.getDueDate() != null) {
            String dateStr = DateUtils.date2LocalDate(issue.getDueDate(), ZoneId.of("+9")).toString();
            updateRow(row, 9, dateStr);
        }
    }

    private static void updateRow(List<Object> row, int index, String value) {
        int rowSize = row.size();

        if (index < rowSize) {
            row.set(index, value);
        } else {
            for (int i = rowSize; i <= index; i++) {
                row.add("");
            }
            row.set(index, value);
        }
    }

    private static Object getDataOfRow(List<Object> row, int index) {
        if (row.size() > index) {
            return row.get(index);
        }
        return null;
    }

    private static BatchUpdateValuesResponse batchUpdateValues(String valueInputOption,
                                                               List<List<Object>> _values) throws GeneralSecurityException, IOException {
        Sheets service = GoogleSheetCommonUtils.getSheetsService();
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
        if (issues == null) {
            return results;
        }
        for (Issue is : issues) {
            if (google_sheet_ticket_ids.contains(is.getId())) {
                continue;
            }
            // ticket moi
            String dateStr = null;
            if (is.getDueDate() != null) {
                dateStr = DateUtils.date2LocalDate(is.getDueDate(), ZoneId.of("+9")).toString();
            }
            List<Object> objects = Arrays.<Object>asList(is.getId(), new String(""), is
                            .getPriorityText(), is.getStatusName(), is.getAssigneeName(), is.getCustomFieldValuesById(148),
                    is.getCustomFieldValuesById(179), is.getSubject(), is.getCustomFieldValuesById(204)
                            .replace(".", ","), dateStr);
            results.add(objects);
        }
        return results;
    }
}
