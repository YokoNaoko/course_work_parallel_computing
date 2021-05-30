package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    static ArrayList<String> tempList = new ArrayList<>();
    static int NUMBER_THREADS = 1;
    static ConcurrentHashMap<String, HashSet<String>> index= new ConcurrentHashMap<>();

    public static void main(String args[]) throws FileNotFoundException, InterruptedException {
        File[] filePath = {new File("datasets/test/neg"),new File("datasets/test/pos"),new File("datasets/train/neg"),
                new File("datasets/train/pos"),new File("datasets/train/unsup")};
//        readDirectory(filePath);
        readDirectoryTwo(filePath);
        findFiles("in Ontario");
    }

    public static void readDirectory(File[] filePath) throws FileNotFoundException, InterruptedException {
        for (File file:filePath) {
            File dir = new File(String.valueOf(file));
            File[] arrFiles = dir.listFiles();
            List<File> lst = Arrays.asList(arrFiles);
            readFile(lst);
        }
    }

    public static void readDirectoryTwo(File[] filePath) throws InterruptedException {
        for (File file:filePath) {
            File dir = new File(String.valueOf(file));
            File[] arrFiles = dir.listFiles();
            List<File> lst = Arrays.asList(arrFiles);
            //System.out.println(lst);
            parallelTwo(lst);
        }
    }

    public static void readFile(List<File> lst) throws FileNotFoundException, InterruptedException {
        for(int i=0;i<lst.size();i++) {
            Scanner scanner = new Scanner(new File(String.valueOf(lst.get(i))));
            tempList = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String temp = scanner.nextLine();
                String[] splitString = temp.toLowerCase().split("\\W+");
                tempList.addAll(Arrays.asList(splitString));
            }
            parallel(String.valueOf(lst.get(i)));
            // System.out.println(lst.get(i) + " : " + tempList);
        }
    }

    public static void parallel(String filename) throws InterruptedException {
        Index[] Index = new Index[NUMBER_THREADS];
        for (int i = 0; i < NUMBER_THREADS; i++) {
            Index[i] = new Index(tempList, tempList.size() / NUMBER_THREADS * i,
                    i == (NUMBER_THREADS - 1) ? tempList.size() : tempList.size() / NUMBER_THREADS * (i + 1), filename,index);
            Index[i].start();
        }
        for (int i = 0; i < NUMBER_THREADS; i++) {
            Index[i].join();
        }
    }

    public static void parallelTwo(List<File> tempList) throws InterruptedException {
        IndexTwo[] IndexTwo = new IndexTwo[NUMBER_THREADS];
        for (int i = 0; i < NUMBER_THREADS; i++) {
            IndexTwo[i] = new IndexTwo(tempList, tempList.size() / NUMBER_THREADS * i,
                    i == (NUMBER_THREADS - 1) ? tempList.size() : tempList.size() / NUMBER_THREADS * (i + 1),index);
            IndexTwo[i].start();
        }
        for (int i = 0; i < NUMBER_THREADS; i++) {
            IndexTwo[i].join();
        }
    }

    public static void findFiles(String message){
        String[] words = message.toLowerCase().split("\\W+");
        if(index.get(words[0])!=null){
            ArrayList<String> fileResult = new ArrayList<>(index.get(words[0]));
            for(String word: words){
                fileResult.retainAll(index.get(word));
            }
            if(fileResult.size()!=0) {
                System.out.println(fileResult);
            }else {
                System.out.println("file not found");
            }
        }else {
            System.out.println("file not found");
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

class IndexTwo extends Thread {
    ConcurrentHashMap<String, HashSet<String>> index;
    List<File> file;
    int startIndex;
    int endIndex;
    IndexTwo(List<File> file, int startIndex, int endIndex, ConcurrentHashMap<String, HashSet<String>> index) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.file = file;
        this.index = index;
    }
    public void run() {
        for (int i = startIndex; i < endIndex; i++) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(String.valueOf(file.get(i))));
                while (scanner.hasNextLine()) {
                    String temp = scanner.nextLine();
                    for(String words : temp.toLowerCase().split("\\W+")){
//                        if(!index.containsKey(words))
//                            index.put(words,new HashSet<>());
                        index.computeIfAbsent(words, k -> new HashSet<>());
                        index.get(words).add(String.valueOf(file.get(i)));
                    }

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}