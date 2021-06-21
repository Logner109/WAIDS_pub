package stu.fei.ids.service;

import org.springframework.stereotype.Service;
import stu.fei.ids.database.DatabaseRules;
import stu.fei.ids.item.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RuleService {

    public List<Rule> get_rules(Map<String, String> queryParams){
        List<Rule> rules = new ArrayList<>();
        DatabaseRules.get_rules(rules, queryParams);
        return rules;
    }

    public void change_rule_status(int id){
        Rule rule = DatabaseRules.get_rule_by_id(id);
        if (rule != null)
            DatabaseRules.change_status(id, !rule.isStatus());
        else
            System.out.println("Error finding rule with ID: " + id);
    }
}
