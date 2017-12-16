package com.company;

import com.sun.tools.corba.se.idl.InterfaceGen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ronny on 12/7/17.
 */
public class Unzipper {
    public static void decompress(String path){
        File file = new File(path);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int dictionary_size = (fileInputStream.read() << 16) + (fileInputStream.read() << 8) + fileInputStream.read();
            int rounded_up_dictionary_size_in_bytes = (dictionary_size + (8 - (dictionary_size%8)))/11;
            System.out.println(rounded_up_dictionary_size_in_bytes);
            HashMap<Character, String> codes = new HashMap<>();
            int decoded = 0;
            int decoded_length = 0;
            int current_needed = 8;
            int current_task = 0;
            int total_bytes_decoded = 0;
            char c = '@';
            while(total_bytes_decoded < rounded_up_dictionary_size_in_bytes) {
                System.out.println("lol");
                while (decoded_length < current_needed) {
                    decoded = decoded << 8;
                    decoded += fileInputStream.read();
                    decoded_length += 8;
                    total_bytes_decoded++;
                    System.out.println("meow");
                }
                if (current_task == 0) {
                    c = (char) (decoded >> (decoded_length - 8));
                    decoded = decoded >> 8;
                    decoded_length -= 8;
                    current_needed = 4;
                    current_task++;
                } else if (current_task == 1) {
                    current_needed = (decoded >> (decoded_length - 4));
                    decoded_length -= 4;
                    decoded = decoded >> 4;
                    current_task++;
                } else if (current_task == 2) {
                    int code_value = (decoded >> (decoded_length - current_needed));
                    decoded_length -= current_needed;
                    decoded = decoded >> current_needed;
                    String binary_code_value = Integer.toBinaryString(code_value);
                    while(binary_code_value.length() < current_needed)
                        binary_code_value = "0" + binary_code_value;
                    codes.put(c, binary_code_value);
                    current_task = 0;
                }
            }
            System.out.println("out");
            Iterator<Character> keys = codes.keySet().iterator();
            while(keys.hasNext()){
                Character character = keys.next();
                System.out.println("Char " + character + " is " + codes.get(character));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        decompress("test.t2n");
    }
}
