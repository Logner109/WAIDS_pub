package stu.fei.ids.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stu.fei.ids.database.DatabaseRules;
import stu.fei.ids.service.RuleService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RuleController {
    private final RuleService ruleService;
    private Map<String, String> default_rule_query;
    private Map<String, String> user_rule_query;

    @Autowired
    public RuleController(RuleService ruleService){
        this.ruleService = ruleService;
        this.user_rule_query = new HashMap<>();
        this.default_rule_query = new HashMap<>();
        init_default_rule_query();
    }

    @GetMapping("/engine/")
    public String rule_engine(Model model){
        if(this.user_rule_query.size() > 0){
            model.addAttribute("rules", ruleService.get_rules(user_rule_query));
            this.user_rule_query = new HashMap<>();
        }else
            model.addAttribute("rules", ruleService.get_rules(default_rule_query));

        return "engine";
    }

    //Add new Rule
    @PostMapping(value="/engine/", consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String add_rule(HttpServletRequest request){
        Map<String, String[]> parameters = request.getParameterMap();
        DatabaseRules.insert_rule(parameters.get("regex")[0], parameters.get("tag")[0], true, parameters.get("description")[0]);
        return "redirect:/engine/";
    }

    //Remove Rule from DB
    @GetMapping("/engine/remove/{id}")
    public String remove_rule(@PathVariable Integer id){
        DatabaseRules.remove_rule(id);
        return "redirect:/engine/";
    }

    //user search
    @PostMapping(value = "/engine/search", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String query(@RequestBody String queryParams){
        init_user_rule_query(queryParams);
        return "redirect:/engine/";
    }

    //Change Rule Status
    @GetMapping("/engine/status/{id}")
    public String status(@PathVariable Integer id){
        ruleService.change_rule_status(id);
        return "redirect:/engine/";
    }

    private void init_default_rule_query(){
        this.default_rule_query.put("tag", "");
        this.default_rule_query.put("status", "");
    }

    private void init_user_rule_query(String input){
        try{
            input = java.net.URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] pairs = input.split("&");
        for(String pair : pairs){
            String[] values = pair.split("=");
            if(values.length == 1)
                this.user_rule_query.put(values[0], "");
            else
                this.user_rule_query.put(values[0], values[1]);
        }
    }
}
