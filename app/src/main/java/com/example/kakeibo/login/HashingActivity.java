package com.example.kakeibo.login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

    public class HashingActivity
    {
        public static String main(String p)
        {
            String passwordHashed = null;

            try
            {
                /*  MessageDigest maken voor hashfunctie MD5
                MD5 is niet meer veilig en moet gecombineerd worden met salting of compleet anddere hash algorytme
                 */

                MessageDigest mdPassword = MessageDigest.getInstance("MD5");

                // voeg de bytes doe die gedigest moeten worden
                mdPassword.update(p.getBytes());

                // get de bytes
                byte[] bytesPassword = mdPassword.digest();

                // converteren van decimaal naar hexadecimaal
                StringBuilder sbPassword = new StringBuilder();
                for (byte aByte : bytesPassword) {
                    sbPassword.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }

                // Get hashed wachtwoord in hex formaat
                passwordHashed = sbPassword.toString();
            } catch (NoSuchAlgorithmException md5Error) {
                // Controleer voor foutmelding wanneer het hashing alghoritme niet gevonden kan worden
                md5Error.printStackTrace();
            }
           return passwordHashed;
        }
    }

