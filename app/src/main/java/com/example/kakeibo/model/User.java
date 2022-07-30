package com.example.kakeibo.model;

import java.util.regex.Pattern;

public class User {

    private String name; // naam
    private String email; // email + unieke identifier
    private String password; // wachtwoord
    private String dateOfBirth; // date of birth


    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public static boolean isValidEmail(String email) {
        {
        String regex = "^.+@.+\\..+$";
        Pattern pattern = Pattern.compile(regex);
        if (email == null)
            return false;
        return pattern.matcher(email).matches();
    }
}

    public static boolean isValidName(String name) {
        {
            String regex = "^[\\p{L} .'-]+$";
            Pattern pattern = Pattern.compile(regex);
            if (name == null)
                return false;
            return pattern.matcher(name).matches();
        }
    }

    public static boolean isValidDateOfBirth(String dateOfBirth) {
        {
            String regex = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
            Pattern pattern = Pattern.compile(regex);
            if (dateOfBirth == null)
                return false;
            return pattern.matcher(dateOfBirth).matches();
        }
    }

    public static boolean isValidPassword(String password) {
        {
            /* uit een breking van de regex controle =
            {.8,} staat voor minimaal acht karakters totaal
            (?=.*?[A-Z]) & (?=.*?[a-z]) staan voor minimaal 1 letters van a-z en minimaal 1 hoofdletter van A-Z
            (?=.*?[0-9]) staat voor minimaal 1 getal
            (?=.*?[?!@$%^&*]) staat voor minimaal 1 speciaal karakter
           */
            String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
            Pattern pattern = Pattern.compile(regex);
            if (password == null)
                return false;
            return pattern.matcher(password).matches();
        }
    }



    }

