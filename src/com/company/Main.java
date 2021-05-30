package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    static ArrayList<String> tempList = new ArrayList<>();
    static int NUMBER_THREADS = 1;
    static ConcurrentHashMap<String, HashSet<String>> index= new ConcurrentHashMap<>();

    public static void main(String args[]) {
        File[] filePath = {new File("datasets/test/neg"),new File("datasets/test/pos"),new File("datasets/train/neg"),
                new File("datasets/train/pos"),new File("datasets/train/unsup")};

    }

    public static void readDirectory(File[] filePath){
        for (File file:filePath) {
            File dir = new File(String.valueOf(file));
            File[] arrFiles = dir.listFiles();
            List<File> lst = Arrays.asList(arrFiles);
        }
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