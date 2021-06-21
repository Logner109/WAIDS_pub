package stu.fei.ids.item;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Report {
    private int id;
    private String frequency;
    private LocalDateTime created;
    private String time;
    private String format;
    private String receiver;
    private final Map<String, String> report_query;
    private LocalDateTime last_performed;
    private LocalDateTime perform;

    public Report(int id, String frequency, String time, String format, String receiver, String created){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.id = id;
        this.frequency = frequency;
        this.created = LocalDateTime.parse(created, formatter);
        this.time = time;
        this.format = format;
        this.receiver = receiver;
        this.last_performed = null;
        this.perform = null;
        this.report_query = new HashMap<>();
    }


    public LocalDateTime getPerform() {
        if(this.last_performed != null)
            this.perform = last_performed;
        else
            this.perform = created;

        switch (frequency){
            case "Daily":
                return perform.plusDays(1);
            case "Weekly":
                return perform.plusDays(7);
            case "Monthly":
                return perform.plusMonths(1);
        }
        return perform;
    }

    public void setPerform(LocalDateTime perform) {
        this.perform = perform;
    }

    public Map<String, String> getReport_query() {
        String time_;
        if(last_performed != null) {
            time_ = this.last_performed.toString();
        }else{
            time_ = this.created.toString();
        }

        time_ = time_.replace("T", " ");
        time_ = time_.substring(0, 19);

        this.report_query.put("from", time_);
        this.report_query.put("to", "");
        this.report_query.put("score", "1");
        this.report_query.put("source_ip", "");
        this.report_query.put("suspicious", "1");

        this.last_performed = LocalDateTime.now();
        return report_query;
    }

    public void setReport_query() {
        //TODO
    }

    public LocalDateTime getLast_performed() {
        return last_performed;
    }

    public void setLast_performed(LocalDateTime last_performed) {
        this.last_performed = last_performed;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getCreated() {
        String simple = this.created.toString().replace("T", " ");
        simple = simple.substring(0, 19);
        return simple;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
