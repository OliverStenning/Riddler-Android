package co.stenning.riddler.data;

public class Player {

    private static final int DEFAULT_HINT_AMOUNT = 3;
    public static final int HINT_REWARD_AMOUNT = 3;


    // Player status variables
    private int questionNumber;
    private int hints;
    private int score;
    private int questionsSinceAd;
    private boolean ratingAsked;

    // Player current question status variables
    private long questionTimeStarted;
    private int questionHintsUsed;
    private int questionIncorrectGuesses;

    //Player overall stats
    private long startTime;
    private int totalIncorrectGuesses;
    private int totalHintsUsed;
    private int totalSkips;

    public Player() {

        // setup default Player status
        setQuestionNumber(0);
        setHints(DEFAULT_HINT_AMOUNT);
        setScore(0);
        setQuestionsSinceAd(0);
        setRatingAsked(false);

        // setup default question status
        setQuestionTimeStarted(System.nanoTime());
        setQuestionHintsUsed(0);
        setQuestionIncorrectGuesses(0);

        // setup overall stats
        setStartTime(System.nanoTime());
        setTotalIncorrectGuesses(0);
        setTotalHintsUsed(0);
        setTotalSkips(0);

    }

    public void incrementQuestionNumber() {
        setQuestionNumber(getQuestionNumber() + 1);
    }

    public void addHints(int amount) {
        setHints(getHints() + amount);
    }

    public void removeHints(int amount) {
        setHints(getHints() - amount);
    }

    public void addScore(int amount) {
        setScore(getScore() + amount);
    }

    public void incrementQuestionsSinceAd() {
        setQuestionsSinceAd(getQuestionsSinceAd() + 1);
    }

    public void incrementHintsUsed() {
        setTotalHintsUsed(getTotalHintsUsed() + 1);
        setQuestionHintsUsed(getQuestionHintsUsed() + 1);
    }

    public void incrementIncorrectGuesses() {
        setTotalIncorrectGuesses(getTotalIncorrectGuesses() + 1);
        setQuestionIncorrectGuesses(getQuestionIncorrectGuesses() + 1);
    }

    public void incrementTotalSkips() {
        setTotalSkips(getTotalSkips() + 1);
    }


    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public int getHints() {
        return hints;
    }

    public void setHints(int hints) {
        this.hints = hints;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getQuestionsSinceAd() {
        return questionsSinceAd;
    }

    public void setQuestionsSinceAd(int questionsSinceAd) {
        this.questionsSinceAd = questionsSinceAd;
    }

    public boolean isRatingAsked() {
        return ratingAsked;
    }

    public void setRatingAsked(boolean ratingAsked) {
        this.ratingAsked = ratingAsked;
    }

    public long getQuestionTimeStarted() {
        return questionTimeStarted;
    }

    public void setQuestionTimeStarted(long questionTimeStarted) {
        this.questionTimeStarted = questionTimeStarted;
    }

    public int getQuestionHintsUsed() {
        return questionHintsUsed;
    }

    public void setQuestionHintsUsed(int questionHintsUsed) {
        this.questionHintsUsed = questionHintsUsed;
    }

    public int getQuestionIncorrectGuesses() {
        return questionIncorrectGuesses;
    }

    public void setQuestionIncorrectGuesses(int questionIncorrectGuesses) {
        this.questionIncorrectGuesses = questionIncorrectGuesses;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getTotalIncorrectGuesses() {
        return totalIncorrectGuesses;
    }

    public void setTotalIncorrectGuesses(int totalIncorrectGuesses) {
        this.totalIncorrectGuesses = totalIncorrectGuesses;
    }

    public int getTotalHintsUsed() {
        return totalHintsUsed;
    }

    public void setTotalHintsUsed(int totalHintsUsed) {
        this.totalHintsUsed = totalHintsUsed;
    }

    public int getTotalSkips() {
        return totalSkips;
    }

    public void setTotalSkips(int totalSkips) {
        this.totalSkips = totalSkips;
    }
}
