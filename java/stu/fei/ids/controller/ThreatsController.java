package stu.fei.ids.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stu.fei.ids.service.RequestService;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ThreatsController {
    private final RequestService requestService;
    private Map<String, String> default_threat_query;

    @Autowired
    public ThreatsController(RequestService requestService) {
        this.requestService = requestService;
        init_default_threat_query();
    }

    @GetMapping("/threats/{page}")
    public String traffic(Model model, @PathVariable int page){
        if(is_empty(this.default_threat_query)){
            init_default_threat_query();
        }
        model.addAttribute("current_page", page);
        model.addAttribute("requests", this.requestService.get_traffic(this.default_threat_query, page));

        return "threats";
    }

    @PostMapping(value = "/threats/", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String query(@RequestBody String queryParams){
        init_user_threat_query(queryParams);
        return "redirect:/threats/1";
    }


    private void init_default_threat_query(){
        String now = LocalDateTime.now().toString();
        now = now.replace("T", " ");
        now = now.substring(0, 11);
        now += "00:00:00";

        this.default_threat_query = new HashMap<>();
        this.default_threat_query.put("from", now);
        this.default_threat_query.put("to", "");
        this.default_threat_query.put("score", "");
        this.default_threat_query.put("source_ip", "");
        this.default_threat_query.put("suspicious", "true");
    }

    private void init_user_threat_query(String input){
        this.default_threat_query = new HashMap<>();
        try{
            input = java.net.URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] pairs = input.split("&");
        for(String pair : pairs){
            String[] values = pair.split("=");
            if(values.length == 1)
                this.default_threat_query.put(values[0], "");
            else
                this.default_threat_query.put(values[0], values[1]);
        }
    }

    private boolean is_empty(Map<String, String> query){
        for (String key : query.keySet()){
            if(query.get(key).length() > 0)
                return false;
        }
        return true;
    }
}
