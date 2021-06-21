package stu.fei.ids.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stu.fei.ids.database.DatabaseReport;
import stu.fei.ids.service.ReportService;

import java.io.IOException;

@Controller
public class ReportsController {
    private final ReportService reportService;

    @Autowired
    public ReportsController(ReportService reportService) {
        this.reportService = reportService;
    }


    @GetMapping("/report/")
    public String report_management(Model model) throws IOException {
        model.addAttribute("reports", reportService.get_reports());
        //reportService.send_report();
        return "report";
    }

    @PostMapping(value = "/report/", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String create_report(@RequestBody String reportParams){
        reportService.add_report_cycle(reportParams);
        return "redirect:/report/";
    }

    //Remove Report cycle from DB
    @GetMapping("/report/remove/{id}")
    public String remove_report(@PathVariable Integer id){
        System.out.println("removing");
        DatabaseReport.remove_report(id);
        return "redirect:/report/";
    }
}
