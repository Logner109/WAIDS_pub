package stu.fei.ids.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String redirect(){
        return "redirect:/main/";
    }

    @GetMapping("/main/")
    public String dashboard(){
        return "main";
    }
}
