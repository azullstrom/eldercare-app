package com.example.eldercare.modules;

public class Meal {
    private String time, toEat, mealType;
    private boolean eaten;

    public Meal() {
        // Default constructor required for Firebase
    }

    // Constructor with all parameters
    public Meal(String time, String toEat, String mealType, boolean eaten) {
        this.time = time;
        this.toEat = toEat;
        this.mealType = mealType;
        this.eaten = eaten;
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

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }
}

