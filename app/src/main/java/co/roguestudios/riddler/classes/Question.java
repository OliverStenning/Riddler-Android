package co.roguestudios.riddler.classes;

public class Question {

    private int number;
    private String question;
    private String[] answers;
    private String[] hint;

    public Question(int number, String question, String[] answers, String[] hint) {
        this.number = number;
        this.question = question;
        this.answers = answers;
        this.hint = hint;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getAnswers() {
        return answers;
    }
    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public String[] getHint() {
        return hint;
    }
    public void setHint(String[] hint) {
        this.hint = hint;
    }
}
