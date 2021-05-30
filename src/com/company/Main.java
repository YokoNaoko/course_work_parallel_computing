package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String args[]) {

    }
}

class Index extends Thread {
    ConcurrentHashMap<String, HashSet<String>> index;
    ArrayList<String> words;
    String filename;
    int startIndex;
    int endIndex;
    Index(ArrayList<String> words,int startIndex,int endIndex, String filename,ConcurrentHashMap<String, HashSet<String>> index){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.words = words;
        this.filename = filename;
        this.index = index;
    }
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
//            if (!index.containsKey(words.get(i))) {
//                index.put(words.get(i), new HashSet<>());
//            }
            index.computeIfAbsent(words.get(i), k -> new HashSet<>());
            index.get(words.get(i)).add(filename);
        }
    }
}