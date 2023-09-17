package com.example.eldercare;

public class Meal {
    private String date, time, toEat, mealType;

    public Meal() {
        // Default constructor required for Firebase
    }

    // Constructor with all parameters
    public Meal(String date, String time, String toEat, String mealType) {
        this.date = date;
        this.time = time;
        this.toEat = toEat;
        this.mealType = mealType;
    }

    // Constructor without the 'toEat' parameter
    public Meal(String date, String time, String mealType) {
        this.date = date;
        this.time = time;
        this.mealType = mealType;
        this.toEat = ""; // Default value, you can change it to something else if needed
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getToEat() {
        return toEat;
    }

    public void setToEat(String meal) {
        this.toEat = meal;
    }
}

