package com.dimaslanjaka.gradle.plugin;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Java program to calculate MD5 hash value
public class MD5 {
    private String text;
    private String md5;

    @SuppressWarnings({"unused"})
    public MD5(String text) {
        this.text = text;
        this.md5 = get(this.text);
    }

    /**
     * Get MD5 hash from given string
     *
     * @param input plaintext to be encoded
     * @return {String}
     */
    public static String get(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Driver code
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = "GeeksForGeeks";
        System.out.println("Your HashCode Generated by MD5 is: " + get(s));
    }

    @SuppressWarnings({"unused"})
    public MD5 init(String text) {
        this.text = text;
        this.md5 = get(text);
        return this;
    }

    @Override
    public String toString() {
        return md5;
    }
}