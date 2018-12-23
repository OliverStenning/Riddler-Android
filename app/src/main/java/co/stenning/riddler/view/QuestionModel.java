package co.stenning.riddler.view;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import co.stenning.riddler.R;
import co.stenning.riddler.classes.Player;
import co.stenning.riddler.classes.Question;

public class QuestionModel extends ViewModel {

    /* Loading Player from File */
    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final boolean RESET_PLAYER = true;

    /* Scoring System Values */
    private static final int MAX_TIME_SCORE = 2000;
    private static final int MAX_GUESSES_SCORE = 2000;
    private static final int MAX_HINT_SCORE = 1000;

    private static final int GUESS_SCORE_REDUCTION = 500;
    private static final int HINT_SCORE_REDUCTION = 500;

    /* Game Data */
    private MutableLiveData<Player> player;
    private ArrayList<Question> questions;

    public MutableLiveData<Player> getPlayer() {
        return player;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }

    /* File Handling Methods */
    public void loadPlayer(Context context) {
        if (player == null) {

            //Initialise player object if not already
            player = new MutableLiveData<>();

            //Check if file exists
            File playerFile = new File(context.getFilesDir(), context.getString(R.string.player_file));
            if (playerFile.exists() && !RESET_PLAYER) {
                try {
                    FileInputStream fileInputStream = context.openFileInput(context.getString(R.string.player_file));
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
                    Type type = new TypeToken<Player>() {}.getType();
                    Player tempPlayer = gson.fromJson(inputStreamReader, type);
                    //Reset ad amount so ads aren't shown when the start app again
                    tempPlayer.setQuestionsSinceAd(0);
                    player.setValue(tempPlayer);
                } catch (IOException e) {
                    Log.e("Loading", "Error loading saved player data");
                }
            } else {
                Player tempPlayer = new Player();
                tempPlayer.setPosition(9);
                player.setValue(tempPlayer);
            }
        }
    }

    public void savePlayer(Context context) {

        String output = gson.toJson(player.getValue());
        try {
            File file = new File(context.getFilesDir(), context.getString(R.string.player_file));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            Writer writer = new OutputStreamWriter(fileOutputStream, "UTF-8");
            writer.write(output);
            writer.flush();
        } catch (IOException e) {
            Log.e("Saving", "Error saving player data");
        }

    }

    public void loadQuestions(Context context) {
        if (questions == null) {
            questions = new ArrayList<>();
            try {
                InputStream inputStream = context.getApplicationContext().getAssets().open(context.getString(R.string.questions_file));
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                Type type = new TypeToken<ArrayList<Question>>(){}.getType();
                questions = gson.fromJson(inputStreamReader, type);
            } catch (IOException e) {
                Log.e("Loading", "Error loading player data");
            }
        }
    }

    /* Game Operation Methods */
    public boolean guess(String guess) {

        guess = guess.toLowerCase();
        int i = 0;
        boolean correct = false;
        int position = player.getValue().getPosition();
        Player tempPlayer = player.getValue();

        while (!correct && i < questions.get(position).getAnswers().length) {
            if (guess.length() > 3) {
                if (guess.equals(questions.get(position).getAnswers()[i])) {
                    correct = true;
                } else {
                    i += 1;
                }
            } else {
                if (guess.contains(questions.get(position).getAnswers()[i])) {
                    correct = true;
                } else {
                    i += 1;
                }
            }
        }

        if (correct) {
            return true;
        } else {
            tempPlayer.incrementIncorrectGuesses();
            player.setValue(tempPlayer);
            return false;
        }

    }

    public void useHint() {
        Player tempPlayer = player.getValue();
        tempPlayer.incrementQuestionHintsUsed();
        tempPlayer.incrementTotalHintsUsed();
        tempPlayer.decrementHints();
        player.setValue(tempPlayer);
    }

    public void useSkip() {
        //TODO implement skip functionality
    }

    public int calculateScore() {
        //start score at maximum and lower it to actual score
        int timeScore = MAX_TIME_SCORE;
        int guessScore = MAX_GUESSES_SCORE;
        int hintScore = MAX_HINT_SCORE;

        Player tempPlayer = player.getValue();

        //reduces score by one for every second since the start of the question
        int secondsSinceStart = (int) ((System.nanoTime() - tempPlayer.getQuestionTimeStarted()) / 1000000000);
        timeScore -= secondsSinceStart;
        //stop score from becoming negative
        if (timeScore < 0)
            timeScore = 0;

        //reduces score based on number of incorrect guesses
        int guessScoreReduction = 0;
        for (int i = 0; i < tempPlayer.getQuestionIncorrectGuesses(); i++) {
            //decrease reduction for each subsequent incorrect guess by half
            //i.e. 1 incorrect = -500, 2 incorrect = -750, 3 incorrect = -875
            //always get a score larger than 1000 to differentiate from skipping question
            guessScoreReduction += GUESS_SCORE_REDUCTION / (Math.pow(2, i));
        }
        guessScore -= guessScoreReduction;

        //reduces score based on number of hints used
        hintScore -= (tempPlayer.getQuestionHintsUsed() * HINT_SCORE_REDUCTION);
        //stop score from becoming negative
        if (hintScore < 0)
            hintScore = 0;

        return timeScore + guessScore + hintScore;
    }

    public void updateScore(int score) {
        Player tempPlayer = player.getValue();
        tempPlayer.addScore(score);
        player.setValue(tempPlayer);
    }

    public int getScore() {
        return player.getValue().getScore();
    }

    public ArrayList<Integer> checkProgressAchievements() {
        //try to unlock achievements in case not signed in when earned
        int position = player.getValue().getPosition();
        ArrayList<Integer> unlockedAchievements = new ArrayList<>();

        //add each achievement user should have unlocked
        if (position >= 10)
            unlockedAchievements.add(R.string.achievement_youre_a_natural);
        if (position >= 20)
            unlockedAchievements.add(R.string.achievement_riddle_lover);
        if (position >= 30)
            unlockedAchievements.add(R.string.achievement_riddle_pro);
        if (position >= 40)
            unlockedAchievements.add(R.string.achievement_riddle_master);
        if (position >= 50)
            unlockedAchievements.add(R.string.achievement_defeat_the_riddler);

        //return list of all progress achievements user should have unlocked
        return unlockedAchievements;
    }

    public void incrementPlayer() {
        Player tempPlayer = player.getValue();
        tempPlayer.incrementPosition();
        tempPlayer.setQuestionTimeStarted(System.nanoTime());
        tempPlayer.setQuestionHintsUsed(0);
        tempPlayer.setQuestionIncorrectGuesses(0);
        player.setValue(tempPlayer);
    }

    public void setFirstStartTime() {
        Player tempPlayer = player.getValue();
        if (tempPlayer.getQuestionTimeStarted() == 0) {
            tempPlayer.setQuestionTimeStarted(System.nanoTime());
            player.setValue(tempPlayer);
        }
    }
}
