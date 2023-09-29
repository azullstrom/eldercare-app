package com.example.eldercare;

public class Meal {
    private String time, toEat, mealType;

    public Meal() {
        // Default constructor required for Firebase
    }

    // Constructor with all parameters
    public Meal(String time, String toEat, String mealType) {
        this.time = time;
        this.toEat = toEat;
        this.mealType = mealType;
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

