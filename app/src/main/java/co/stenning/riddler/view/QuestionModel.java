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

    private final static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final boolean RESET_PLAYER = true;

    private MutableLiveData<Player> player;
    private ArrayList<Question> questions;

    public MutableLiveData<Player> getPlayer() {
        return player;
    }
    public ArrayList<Question> getQuestions() {
        return questions;
    }

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
            tempPlayer.incrementTotalIncorrectGuesses();
            player.setValue(tempPlayer);
            return false;
        }

    }

    public void incrementPlayer() {
        Player tempPlayer = player.getValue();
        tempPlayer.incrementPosition();
        tempPlayer.setQuestionHintsUsed(0);
        player.setValue(tempPlayer);
    }

    public void useHint() {
        Player tempPlayer = player.getValue();
        tempPlayer.incrementQuestionHintsUsed();
        tempPlayer.incrementTotalHintsUsed();
        tempPlayer.decrementHints();
        player.setValue(tempPlayer);
    }

}
