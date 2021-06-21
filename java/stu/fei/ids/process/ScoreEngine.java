package stu.fei.ids.process;

import stu.fei.ids.item.Rule;
import stu.fei.ids.database.DatabaseRules;
import stu.fei.ids.item.ScoreResult;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreEngine {
    private final List<Rule> rules;
    private final List<String> tags;//pre triedu Request
    private final ScoreResult result;

    public ScoreEngine() {
        this.rules = new ArrayList<>();
        DatabaseRules.get_rules(this.rules, all_rules_query());
        this.tags = new ArrayList<>();
        this.result = new ScoreResult();
    }

    public ScoreResult score(String request) {
        request = delete_modifications(request);//for input like UNI/*x*/ON SELECT path

        for (Rule r : this.rules) {
            if (r.isStatus())
                check_presence(request, r);
        }

        result.setTags(this.tags);
        result.setFinal_score(get_score());

        return result;
    }

    private void check_presence(String input, Rule rule) {
        Pattern pattern = Pattern.compile(rule.getRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        boolean result = matcher.find();

        if (result)
            this.tags.add(rule.getId() + ":" + rule.getTag());
    }

    private Map<String, String> all_rules_query() {
        Map<String, String> query = new HashMap<>();
        query.put("tag", "");
        query.put("status", "");
        return query;
    }

    private Double get_score() {
        double score = 0.0;
        Pattern pattern = Pattern.compile("paranoid", Pattern.CASE_INSENSITIVE);

        for (String tag : this.tags) {
            Matcher matcher = pattern.matcher(tag);
            if (matcher.find())
                score += 50;
            else
                score += 100;
        }

        return score;
    }


    // odstranenie komentarov
    private String delete_modifications(String request) {

        request = request.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)"," ");

        return request;
    }

}
