public class LetterScore {
    private char letter;
    private  Score score;

    @Override
    public String toString() {
        return "LetterScore{" +
                "letter=" + letter +
                ", score=" + score +
                '}';
    }

    public LetterScore(char letter, Score score){
        this.letter = letter;
        this.score = score;
    }

    public char getLetter() {
        return letter;
    }

    public Score getScore() {
        return score;
    }

}