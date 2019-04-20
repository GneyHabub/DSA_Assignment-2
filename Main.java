import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
/**
 * Alexander Krivonosov Group 6
 * Tests were taken from the examples in assigment and codeforces. Also, when I had to understand
 * why my code creates new words on the test 5 in 3rd task, I used this test:
 * i am a simple computer user which specialization is not computer science but a company management of the people, who like me, the math and suffer from it. this is awesome! lu, lu
 * my id is the likey, compu thi. d li th ya wanna cofe?
 * The answer contained words which were not in the dictionsry.
 * I realised that the function replace changes not separate words, but first found sequence, so it was breaking the answer.
 */
public class Main {
    public static void main(String[] args) {
        //task3();
        //task2();
        //task1();
    }

    private static void task3(){
        Scanner input = new Scanner(System.in);
        String inLine = input.nextLine();
        String[] rowDict = inLine.split("[^a-z]+"); // The entire dictionary is splitted by spaces
        List<dictElement> dict = new ArrayList<>();

        for (String word: rowDict){ // Here I calculate the frequency for each word
            if (elementIsIn(word, dict) > -1){
                dict.get(elementIsIn(word, dict)).freq++;
            } else{
                dict.add(new dictElement());
                dict.get(dict.size() - 1).element = word; // Here I create the final dictionary using class dictElement
                dict.get(dict.size() - 1).freq = 1;
            }
        }

        String inLine1 = input.nextLine();
        String[] words = inLine1.split("[^a-z]+"); // Array of words in the string
        String[] punctuationMarks = inLine1.split("[a-z]+"); // Array of everything else
        for (int i  = 0; i < words.length; i++){
            words[i] = bestCorrection(words[i], dict); //Each word is now corrected
        }

        // Here is a bit complicated output
        if (words.length == punctuationMarks.length - 1){
            for (int i  = 0; i < words.length; i++) {
                System.out.print(words[i] + punctuationMarks[i+1]); // I print word and then everything after it
            }
        }else {
            // Another case of input structure
            for (int i  = 0; i < words.length - 1; i++) {
                System.out.print(words[i] + punctuationMarks[i+1]);
            }
            System.out.print(words[words.length - 1]);
        }
    }

    private static void task2(){
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        input.nextLine();
        String[] dict = input.nextLine().split(" ");
        String word = input.nextLine();
        Arrays.sort(dict); // For output sequence to be in lexicographic order, I simply sort the dictionary before the main algorithm's process.

        int[] setOfDist = new int[n];
        for (int i = 0; i < n; i++){
            setOfDist[i] = estimate(word, dict[i]); // Levenshtein distance from each word in dictionary
        }
        int minimal = word.length();
        for (int i = 0; i < n; i++){
            if (setOfDist[i] < minimal){
                minimal = setOfDist[i]; // Find the minimum distance
            }
        }
        List<String> ans = new ArrayList<>();
        for (int i = 0; i < n; i++){
            if (setOfDist[i] == minimal){
                ans.add(dict[i]); // Retrieve all words with the minimum distance
            }
        }
        for (int i = 0; i < ans.size() - 1; i++){
            System.out.print(ans.get(i) + " "); // Output them
        }
        System.out.print(ans.get(ans.size() - 1)); // Avoid space in the end
    }

    private static void task1(){
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        input.nextLine();
        int[] ans = new int[n];
        for (int  i = 0; i < n; i++){
            String[] words = input.nextLine().split(" ");
            ans[i] = estimate(words[0], words[1]); // Levenshtein distance for each pair of words
        }
        for (int  i = 0; i < n; i++){
            System.out.println(ans[i]); // Output
        }
    }

    /**
     * I use Optimal String Alignment algorithm to compute the Levenshtein distance. We could do straightforward computations but it will take a lot of time.
     * The simple algorithm computes the same result many times. Using dynamic programming approach, we write results of our computations to the 2D matrix and then
     * retrieve them from it, when needed.
     */
    private static int estimate(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int[][] matrix = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++){
            matrix[i][0] = i;
        }
        for (int i = 1; i <= len2; i++){
            matrix[0][i] = i;
        }
        for (int i = 1; i <= len1; i++){
            for (int j = 1; j <= len2; j++){
                int m;
                if (word1.charAt(i - 1) == word2.charAt(j - 1)){
                    m = 0;
                } else {
                    m = 1;
                }
                matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + m);
                if (i > 1 && j > 1 && word1.charAt(i - 1) == word2.charAt(j - 2) && word1.charAt(i - 2) == word2.charAt(j - 1)){
                    matrix[i][j] = Math.min(matrix[i][j], matrix[i-2][j-2] + m);
                }
            }
        }
        return matrix[len1][len2];
    }

    /**
     * This function is used in 3rd task. It suggests the best correction for a given word from a given dictionary.
     */
    private static String bestCorrection(String word, List<dictElement> dict){
        List<dictElement> bestSuggestions = new ArrayList<>(); //I use dynamic array, since I don't know how many words can be suitable.
        int minimal = word.length();
        for (dictElement el: dict){
            if (estimate(word, el.element) < minimal){
                minimal = estimate(word, el.element); // Here I find minimal value of OSA distance
            }
        }
        for (dictElement el: dict){
            if (estimate(word, el.element) == minimal){
                bestSuggestions.add(el); // Here I just fill this array with words, which OSA distance is minimal
            }
        }
        int maximalValue = -1;
        String maximal = null;
        for (dictElement el: bestSuggestions){
            if (el.freq > maximalValue){
                maximalValue = el.freq; // Here I find word with highest frequency
                maximal = el.element;
            }
        }
        return maximal;
    }

    /**
     * Simple function which is used to check if element is in an array of dictElements.
     */
    private static int elementIsIn(String word, List<dictElement> dict){
        int res = -1;
        for (int i = 0; i < dict.size(); i++){
            String el = dict.get(i).element;
            if(el.equals(word)){
                res = i;
            }
        }
        return res;
    }
}

/**
 * Simple auxiliary class for simplifying the computations
 */
 class dictElement{

    String element;
    int freq;
}
