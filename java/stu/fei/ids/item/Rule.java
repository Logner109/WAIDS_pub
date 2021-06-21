package stu.fei.ids.item;

import lombok.Data;

@Data
public class Rule {
    private int id;
    private String regex;
    private String tag;
    private String description;
    private boolean status;

    public Rule(int id, String regex, String tag, boolean status, String description){
        this.id = id;
        this.regex = regex;
        this.tag = tag;
        this.description = description;
        this.status = status;
    }
}
