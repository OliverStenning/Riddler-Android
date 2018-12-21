package co.stenning.riddler.classes;

public class Player {

    private static final int DEFAULT_HINT_AMOUNT = 5;

    private int position;
    private int hints;
    private int questionHintsUsed;
    private int questionsSinceAd;
    private boolean ratingAsked;

    //Player stats
    private long startTime;
    private int totalIncorrectGuesses;
    private int totalHintsUsed;
    private int totalSkips;

    public Player() {

        //Setup default player
        setPosition(0);
        setHints(DEFAULT_HINT_AMOUNT);
        setQuestionHintsUsed(0);
        setQuestionsSinceAd(0);
        setRatingAsked(false);

        //Reset stats
        setStartTime(System.nanoTime());
        setTotalIncorrectGuesses(0);
        setTotalHintsUsed(0);
        setTotalSkips(0);

    }

    public void addHints(int amount) {
        this.hints += amount;
    }
    public void decrementHints() {
        this.hints -= 1;
    }
    public void incrementQuestionHintsUsed() {
        this.questionHintsUsed += 1;
    }
    public void incrementTotalHintsUsed() {
        this.totalHintsUsed += 1;
    }
    public void incrementPosition() {
        this.position += 1;
    }
    public void incrementTotalIncorrectGuesses() {
        this.totalIncorrectGuesses += 1;
    }


    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public int getHints() {
        return hints;
    }
    public void setHints(int hints) {
        this.hints = hints;
    }

    public int getQuestionHintsUsed() {
        return questionHintsUsed;
    }
    public void setQuestionHintsUsed(int questionHintsUsed) {
        this.questionHintsUsed = questionHintsUsed;
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
