package stu.fei.ids.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import stu.fei.ids.database.DatabaseStats;

import java.util.Map;

@RestController
public class StatsController {
    @GetMapping("/stats1")
    public ResponseEntity<Map<String, String>> read1(){
        Map<String, String> stats = DatabaseStats.get_stats1();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/stats2")
    public ResponseEntity<Map<String, String>> read2(){
        Map<String, String> stats = DatabaseStats.get_stats2();
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/stats3")
    public ResponseEntity<Map<String, String>> read3(){
        Map<String, String> stats = DatabaseStats.get_stats3();
        return ResponseEntity.ok(stats);
    }
}
