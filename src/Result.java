import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class Result {

    private LetterScore[] letterScores = new LetterScore[5];

    private boolean isCorrect = false;

    public Result(org.json.JSONArray jsonArray) {
        int correctLetters = 0;
        for(int i = 0; i < 5; i++){
            JSONObject individualScore = jsonArray.getJSONObject(i);
            char letter  = individualScore.getString("char").charAt(0);
            JSONObject score_json = individualScore.getJSONObject("scoring");
            boolean in_word = score_json.getBoolean("in_word");
            boolean correct_idx = score_json.getBoolean("correct_idx");
            Score score = Score.WRONG;
            if (in_word)
                score = Score.WRONG_POSITION;
            if (correct_idx) {
                score = Score.RIGHT;
                correctLetters++;
            }

            letterScores[i] = new LetterScore(letter, score);
        }
        if (correctLetters == 5){
            isCorrect = true;
        }
    }

    public Result(LetterScore[] letterScores){
        this.letterScores = letterScores;
        int correctLetters = 0;
        for (LetterScore letterScore: letterScores){
            if (letterScore.getScore() == Score.RIGHT)
                correctLetters++;
        }
        if (correctLetters == 5){
            isCorrect = true;
        }
    }

    public LetterScore[] getLetterScores() {
        return letterScores;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public String toString() {
        return "Result{" +
                "letterScores=" + Arrays.toString(letterScores) +
                '}';
    }
}
