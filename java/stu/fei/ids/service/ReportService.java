package stu.fei.ids.service;

import org.springframework.stereotype.Service;
import stu.fei.ids.database.DatabaseReport;
import stu.fei.ids.process.ReportGenerator;
import stu.fei.ids.item.Report;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Service
public class ReportService {

    public void add_report_cycle(String reportParams){
        reportParams = decode(reportParams);
        Hashtable<String, String> params = parse(reportParams);
        DatabaseReport.insert_report(params.get("frequency"), params.get("generate"),
                params.get("format"), params.get("email"));
    }

    public List<Report> get_reports(){
        List<Report> reportList = new ArrayList<>();
        DatabaseReport.get_reports(reportList);
        return reportList;
    }

    public void send_report() throws IOException {
        List<Report> reportList = new ArrayList<>();
        DatabaseReport.get_reports(reportList);

        for(Report r : reportList){
            if(r.getPerform().isEqual(LocalDateTime.now()) || r.getPerform().isAfter(LocalDateTime.now())) {
                ReportGenerator reportGenerator = new ReportGenerator();
                reportGenerator.create_file(r);
                reportGenerator.send_report(r);
            }
        }
    }

    private String decode(String input){
        try{
            input = java.net.URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    private Hashtable<String, String> parse(String input){
        Hashtable<String, String> parsed = new Hashtable<>();

        String[] pairs = input.split("&");
        for(String pair : pairs){
            String[] values = pair.split("=");
            if(values.length > 1)
                parsed.put(values[0], values[1]);
            else
                parsed.put(values[0], "");
        }

        return parsed;
    }
}
