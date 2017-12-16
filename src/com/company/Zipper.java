package com.company;

import java.io.*;
import java.util.*;

public class Zipper {
    public static void Compress(String path){
        try {
            HashMap<Character, Integer> charCount = new HashMap<>();
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            while(fileInputStream.available() > 0){
                Character c = (char)fileInputStream.read();
                if(charCount.containsKey(c))
                    charCount.put(c, charCount.get(c) + 1);
                else
                    charCount.put(c, 1);
            }

            ////////Counting Characters////////////////////////////////////////////////////////////////////////////////

            PriorityQueue<Node> queue = new PriorityQueue<>(charCount.size(), (o1, o2) -> {
                if (o1.freq < o2.freq)
                    return  -1;
                else if (o1.freq > o2.freq)
                    return 1;
                else return 0;
            });

            Iterator<Character> keys = charCount.keySet().iterator();
            while(keys.hasNext()){
                Character c = keys.next();
                Node n = new Node();
                n.c = c + "";
                n.freq = charCount.get(n.c.charAt(0));
                n.isLeaf = true;
                queue.add(n);
            }
            while(queue.size() > 1) {
                Node a = queue.poll();
                Node b = queue.poll();
                queue.add(a.add(b));
            }
            HashMap<Character, HuffmanCharacter> codes = new HashMap<>();
            traverse(codes, queue.poll(), "");

            /////////Constructing and Traversing Huffman Traversing//////////////////////////////////////////////////////////////////////////////

            int dictionary_size = 0;
            keys = codes.keySet().iterator();
            while(keys.hasNext()){
                Character c = keys.next();
                dictionary_size += (8 + 3 + codes.get(c).length);
            }
            File file2 = new File("test.t2n");
            FileOutputStream fileOutputStream = new FileOutputStream(file2);

            fileOutputStream.write(dictionary_size >> 24);
            fileOutputStream.write(dictionary_size >> 16);
            fileOutputStream.write(dictionary_size >>  8);
            fileOutputStream.write(dictionary_size);

            System.out.println("Dictionary Size: " + dictionary_size);

            //////////Calculating and writing Dictionary Size/////////////////////////////////////////////////////////////////////////////

            int encoded = 0;
            int encoded_length = 0;
            keys = codes.keySet().iterator();
            while(keys.hasNext()){
                Character c = keys.next();
                HuffmanCharacter hc = codes.get(c);

                encoded = encoded << 8;
                encoded_length += 8;
                encoded += c;

                encoded = encoded << 3;
                encoded_length += 3;
                encoded += hc.length;

                encoded = encoded << hc.length;
                encoded_length += hc.length;
                encoded += hc.value;

                while(encoded_length >= 8){
                    int data = encoded >> (encoded_length - 8);
                    fileOutputStream.write(data);
                    encoded_length -= 8;
                    encoded = encoded >> 8;
                }
            }

            //////////Writing Dictionary/////////////////////////////////////////////////////////////////////////////

            int code_size = 0;
            keys = codes.keySet().iterator();
            while(keys.hasNext()) {
                char c = keys.next();
                code_size += (codes.get(keys.next()).length * charCount.get(c));
            }

            fileOutputStream.write(code_size >> 24);
            fileOutputStream.write(code_size >> 16);
            fileOutputStream.write(code_size >>  8);
            fileOutputStream.write(code_size);

            //////////Calculating and Writing code size/////////////////////////////////////////////////////////////////////////////

            fileInputStream = new FileInputStream(file);
            while(fileInputStream.available() > 0) {
                char c = (char) fileInputStream.read();
                HuffmanCharacter hc = codes.get(c);

                encoded = encoded << hc.length;
                encoded_length += hc.length;
                encoded += hc.value;

                while (encoded_length >= 8) {
                    int data = encoded >> (encoded_length - 8);
                    fileOutputStream.write(data);
                    encoded_length -= 8;
                    encoded = encoded >> 8;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class HuffmanCharacter{
        int value;
        int length;

        public HuffmanCharacter(int value, int length) {
            this.value = value;
            this.length = length;
        }
    }

    public static void main(String[] args) throws IOException {
        Compress("test.txt");
        FileInputStream fileInputStream = new FileInputStream(new File("test.t2n"));
        String data = "";
        while(fileInputStream.available() > 0){
            String string_data = Integer.toBinaryString(fileInputStream.read());
            while(string_data.length() < 8){
                string_data = "0" + string_data;
            }
            System.out.print(string_data);
        }
        System.out.println("\nDone");
    }

    static class Node{
         String c;
         int freq;
         boolean isLeaf;
         Node add(Node b){
             Node n = new Node();
             n.c = c + b.c;
             n.freq = freq + b.freq;
             n.left = this;
             n.right = b;
             return n;
         }
         Node left;
         Node right;

         public String toString(){
             return c;
         }
    }

    public static void traverse(HashMap<Character, HuffmanCharacter> map, Node head, String pathTillHere){
        if(!head.isLeaf){
            traverse(map, head.left, pathTillHere + "0");
            traverse(map, head.right, pathTillHere + "1");
        } else {
            map.put(head.c.charAt(0), new HuffmanCharacter(Integer.parseInt(pathTillHere, 2), pathTillHere.length()));
            System.out.println("\t" + head.c + ' ' + pathTillHere);
        }
    }
}
/*
    1: 001
    2: 010
    3: 011
    4: 100
    5: 101
    6: 110
    7: 111
    8: 000
 */