package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private ArrayList<String> wordList;
    private HashSet<String> wordSet;
    private HashMap<String, ArrayList<String>> lettersToWord;
    private HashMap<Integer, ArrayList<String>> sizeToWords;
    private int wordLength;


    AnagramDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        String line;
        wordList = new ArrayList<String>();
        wordSet = new HashSet<String>();
        lettersToWord = new HashMap<String, ArrayList<String>>();
        sizeToWords = new HashMap<Integer, ArrayList<String>>();
        wordLength = DEFAULT_WORD_LENGTH;

        while ((line = in.readLine()) != null) {
            String word = line.trim();

            // Add word to wordList
            wordList.add(word);

            // Add word to wordSet;
            wordSet.add(word);

            // Get sorted word
            String sortedWord = sortLetters(word);

            // Check if sortedWord exists in lettersToWord
            if (lettersToWord.containsKey(sortedWord)) {

                // Get anagrams mapped to sortedWord
                ArrayList<String> anagrams = lettersToWord.get(sortedWord);

                // Add current word
                anagrams.add(word);

                // Map anagrams back to sortedWord
                lettersToWord.put(sortedWord, anagrams);

            }
            // sortedWord doesn't exist in lettersToWord
            else {

                // Create anagrams list
                ArrayList<String> anagrams = new ArrayList<String>();

                // Add current word
                anagrams.add(word);

                // Map anagrams to sortedWord
                lettersToWord.put(sortedWord, anagrams);

            }

            // Add word to sizeToWords
            int length = word.length();
            if (sizeToWords.containsKey(length)) {
                ArrayList<String> words = sizeToWords.get(length);
                words.add(word);
                sizeToWords.put(length, words);
            } else {
                ArrayList<String> words = new ArrayList<String>();
                words.add(word);
                sizeToWords.put(length, words);
            }
        }
    }

    boolean isGoodWord(String word, String base) {
        // word => word entered by user
        // base => the base word for which user has to form anagrams

        // Check if base is a substring of word
        if (word.contains(base)) {
            return false;
        }

        // Check if word is a valid word
        if (wordSet.contains(word)) {
            return true;
        }

        // Word is invalid
        return false;
    }

    public ArrayList<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();

        // Sort targetWord
        targetWord = sortLetters(targetWord);

        // Iterate over all the words in wordList
        for (int i = 0; i < wordList.size(); i++) {

            // Get word at i-th position
            String word = wordList.get(i);

            // Check if the lengths are equal
            if (word.length() == targetWord.length()) {

                // Sort word
                String sortedWord = sortLetters(word);

                // If sortedWord and targetWord match, targetWord and word are anagrams
                if (targetWord.equals(sortedWord)) {
                    // Add to result
                    result.add(word);
                }

            }

        }

        return result;

    }

    private String sortLetters(String word) {
        // Convert word to a character array
        char[] chars = word.toCharArray();

        //sort the characters
        Arrays.sort(chars);

        //convert characters into string
        String sortedWord = new String(chars);

        //return the string value
        return sortedWord;
    }

    ArrayList<String> getAnagramsWithOneMoreLetter(String word) {
        // Instantiate result
        ArrayList<String> result = new ArrayList<String>();

        // Add one letter to word iteratively
        for (char i = 'a'; i <= 'z'; i++) {

            // Add character
            String newWord = word + i;

            // Sort newWord
            String sortedNewWord = sortLetters(newWord);

            String b = "false";
            if (lettersToWord.containsKey(sortedNewWord)) {
                b = "true";
            }

            // If anagrams exist for sortedNewWord, add them to result
            ArrayList<String> anagrams;
            if (lettersToWord.containsKey(sortedNewWord)) {
                // Anagrams exist
                anagrams = lettersToWord.get(sortedNewWord);
            } else {
                // Anagrams don't exist
                anagrams = new ArrayList<String>();
            }
            for (int x = 0; x < anagrams.size(); x++) {
                // Get word
                String wordToAdd = anagrams.get(x);

                // If word is permissible, add it to result
                if (isGoodWord(wordToAdd, word)) {
                    result.add(wordToAdd);
                }
            }
        }
        return result;

    }

    String pickGoodStarterWord() {

        // Create empty randomWord
        String randomWord = "";
        ArrayList<String> anagrams = new ArrayList<String>();

        // Iterate until we find a word with the number of anagrams that matches criteria
        while (anagrams.size() < MIN_NUM_ANAGRAMS) {

            // Reset randomWord and anagrams
            randomWord = "";
            anagrams = new ArrayList<String>();

            // Get randomWord until the word matches criteria
            while (randomWord.length() != wordLength) {
                randomWord = wordList.get(random.nextInt(wordList.size()));
                Log.v("randomWord", randomWord);
            }

            // Get anagrams with one more letter of randomWord
            anagrams = getAnagramsWithOneMoreLetter(randomWord);
            Log.v("anagrams.size", new Integer(anagrams.size()).toString());

        }

        // Increment wordLength
        if (wordLength != MAX_WORD_LENGTH) {
            wordLength++;
        }

        return randomWord;

    }
}