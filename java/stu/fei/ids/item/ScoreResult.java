package stu.fei.ids.item;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreResult {
    private List<String> tags;
    private Double final_score;

    public ScoreResult(){
        tags = new ArrayList<>();
        final_score = 0.0;
    }
}
