package stu.fei.ids;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import stu.fei.ids.service.ReportService;
import stu.fei.ids.service.RequestService;
import stu.fei.ids.service.RuleService;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final RequestService requestService;
    private final RuleService ruleService;
    private final ReportService reportService;

    public Main(RequestService requestService, RuleService ruleService, ReportService reportService){
        this.ruleService = ruleService;
        this.reportService = reportService;
        this.requestService = requestService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
