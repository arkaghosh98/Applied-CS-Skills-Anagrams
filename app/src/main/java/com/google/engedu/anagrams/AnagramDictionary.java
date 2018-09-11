/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private int wordLength = DEFAULT_WORD_LENGTH;

    private ArrayList<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String> > lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String> > sizeToWords = new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while ((line = in.readLine()) != null) {
            String word = line.trim();
            String key = sortLetters(word);
            wordList.add(word);
            wordSet.add(word);
            if (sizeToWords.containsKey(word.length())) {
                sizeToWords.get(word.length()).add(word);
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(word);
                sizeToWords.put(word.length(), list);
            }
            if (lettersToWord.containsKey(key)) {
                lettersToWord.get(key).add(word);
            } else {
                ArrayList<String> list = new ArrayList<>();
                list.add(word);
                lettersToWord.put(key, list);
            }
        }
        /*
        HashSet<String> badKeys = new HashSet<>();
        Log.v("---------", String.valueOf(wordList.size()));
        for (int i = 0, wordSize = wordList.size(); i < wordSize; i++) {
            String word = wordList.get(i);
            String key = sortLetters(word);
            if(!badKeys.contains(key) && lettersToWord.get(key).size() < MIN_NUM_ANAGRAMS) {
                badKeys.add(key);
                ArrayList<String> bad = lettersToWord.get(key);
                Log.v("---------", String.valueOf(wordList.size()) + "   " + i);
                Log.v("---------", "Hello");
                sizeToWords.get(key.length()).removeAll(bad);
            }
        }
        */
    }

    public boolean isGoodWord(String word, String base) {
        return wordSet.contains(word) && !word.contains(base);
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result;
        String key = sortLetters(targetWord);
        result = lettersToWord.get(key);
        result.remove(targetWord);
        return result;
    }

    private String sortLetters(String string) {
        char[] tempString = string.toCharArray();
        Arrays.sort(tempString);
        return new String(tempString);
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>(26);
        ArrayList<String> invalid = new ArrayList<>();
        for (char a = 'a'; a <= 'z'; a++) {
            String sortedWords = sortLetters(word+a);
            words.add(sortedWords);
        }
        for (String key : words) {
            if (lettersToWord.containsKey(key))
                result.addAll(lettersToWord.get(key));
        }
        for (String string : result)
            if(string.contains(word))
                invalid.add(string);
        result.removeAll(invalid);
        return result;
    }

    public String pickGoodStarterWord() {
        String goodStarterWord;
        ArrayList<String> list = sizeToWords.get(wordLength);
        int initialIndex = random.nextInt(list.size());
        int index = initialIndex;
        while(true) {
            String word = list.get(index);
            String key = sortLetters(word);
            Log.v("------------", "Hello : listSize: " + list.size() + " index: " + index + " word : " + word + " size : " + lettersToWord.get(key).size());
            if (lettersToWord.get(key).size() >= MIN_NUM_ANAGRAMS) {
                goodStarterWord = word;
                break;
            }
            index = (index + 1)%list.size();
            if(index == initialIndex) {
                wordLength++;
                list = sizeToWords.get(wordLength);
                initialIndex = random.nextInt(list.size());
                index = initialIndex;
            }
        }
        if(wordLength < MAX_WORD_LENGTH)
            wordLength++;
        return goodStarterWord;
    }
}
