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
public class TrafficController {
    private final RequestService requestService;
    private Map<String, String> search_query;

    @Autowired
    public TrafficController(RequestService requestService) {
        this.requestService = requestService;
        init_search_query();
    }

    @GetMapping("/traffic/{page}")
    public String traffic(Model model, @PathVariable String page){
        if(is_empty(this.search_query)){
            init_search_query();
        }

        model.addAttribute("current_page", Integer.parseInt(page));
        model.addAttribute("requests", this.requestService.get_traffic(this.search_query, Integer.parseInt(page)));

        return "traffic";
    }

    @PostMapping(value = "/traffic/", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String query(@RequestBody String queryParams){
        init_user_query(queryParams);
        return "redirect:/traffic/1";
    }

    private void init_user_query(String input){
        this.search_query = new HashMap<>();
        try{
            input = java.net.URLDecoder.decode(input, StandardCharsets.UTF_8.name());
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] pairs = input.split("&");
        for(String pair : pairs){
            String[] values = pair.split("=");
            if(values.length == 1)
                this.search_query.put(values[0], "");
            else
                this.search_query.put(values[0], values[1]);
        }
    }

    private void init_search_query(){
        String now = LocalDateTime.now().toString();
        now = now.replace("T", " ");
        now = now.substring(0, 11);
        now += "00:00:00";

        this.search_query = new HashMap<>();
        this.search_query.put("from", now);
        this.search_query.put("to", "");
        this.search_query.put("score", "");
        this.search_query.put("source_ip", "");
        this.search_query.put("suspicious", "");
    }

    private boolean is_empty(Map<String, String> query){
        for (String key : query.keySet()){
            if(query.get(key).length() > 0)
                return false;
        }
        return true;
    }
}
