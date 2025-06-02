import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WorldeSolver {

    private String filteredPath = "available_words.txt";

    private HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();


    public WorldeSolver(String file) throws IOException {
        this.reduceToFiveLetterWords(file);
    }

    public void reduceToFiveLetterWords(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        FileWriter fileWriter = new FileWriter(filteredPath);
        while ((line = br.readLine()) != null) {
            if (line.chars().allMatch(Character::isLetter) && (line.length() == 5)) {
                fileWriter.write(line + "\n");
            }
        }
        fileWriter.close();
    }

    public String solveWordle(String startWordle) throws IOException, InterruptedException {
        Result result = tryWordle(startWordle);
        String line;
        LetterScore letterScores [] = result.getLetterScores();
        int trys = 0;
        Logger logger = Logger.getLogger(WorldeSolver.class.getName());
        ArrayList<String> words = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filteredPath));
        while ((line = br.readLine()) != null) {
            words.add(line);
        }
        br.close();

        while (trys < 12) {
            if (trys > 0)
                result = tryWordle(words.get(0));
            if (result.isCorrect())
                return words.get(0);
            letterScores = result.getLetterScores();
            ArrayList<String> toRemove = new ArrayList<>();
            for (String word : words) {
                for (int i = 0; i < letterScores.length; i++) {
                    LetterScore letterScore = letterScores[i];
                    char c = letterScore.getLetter();
                    if (letterScore.getScore() == Score.RIGHT && word.charAt(i) != letterScore.getLetter()) {
                        //logger.info(word + " does not match " + letterScore.getLetter() + " at position " + i);
                        toRemove.add(word);
                        break;
                    } else if (letterScore.getScore() == Score.WRONG_POSITION && word.indexOf(letterScore.getLetter()) == -1) {
                        //logger.info(word + " does not contain " + letterScore.getLetter());
                        toRemove.add(word);
                        break;
                    } else if (letterScore.getScore() == Score.WRONG_POSITION && word.indexOf(letterScore.getLetter()) == i) {
                        //logger.info(word + " contains " + letterScore.getLetter() + " at position " + i + " which is wrong.");
                        toRemove.add(word);
                        break;
                    } else if (letterScore.getScore() == Score.WRONG && word.indexOf(letterScore.getLetter()) != -1) {
                        //logger.info(word + " contains " + letterScore.getLetter() + " which is wrong.");
                        toRemove.add(word);
                        break;
                    }
                }

            }

            words.removeAll(toRemove);
            trys ++;


        }

        return line;
    }

    public Result tryWordle(String wordle) throws IOException, InterruptedException {
        System.out.println("Trying " + wordle);
        org.json.JSONObject json = new JSONObject();
        json.put("guess", wordle);



        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://wordle-api.vercel.app/api/wordle"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject reply = new JSONObject(response.body());

        LetterScore[] letterScores = new LetterScore[5];
        for (int i = 0; i < letterScores.length; i++) {
            letterScores[i] = new LetterScore(wordle.charAt(i), Score.RIGHT);
        }


        Result result = new Result(letterScores);

        try {
            org.json.JSONArray characterInfo =  new JSONArray(reply.getJSONArray("character_info"));
            result = new Result(characterInfo);
        } catch (JSONException e) {
            System.out.println("no character info returned");
        }




        return result;
    }


}
