import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SolvingProgram {

    public static void main(String[] args) throws IOException, InterruptedException {
        WorldeSolver worldeSolver = new WorldeSolver("20k.txt");
        System.out.println("Todays Wordle is " + worldeSolver.solveWordle("hello"));

    }
}
