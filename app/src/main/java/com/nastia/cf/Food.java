package com.nastia.cf;

import java.util.Objects;

public class Food {

    String name;
    Long calories;
    String unit;

    public Food(String name, Long calories, String unit) {
        this.name = name;
        this.calories = calories;
        this.unit=unit;
    }

    public String getName() {
        return name;
    }

    public Long getCalories() {
        return calories;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Objects.equals(name, food.name) &&
                Objects.equals(calories, food.calories) &&
                Objects.equals(unit, food.unit);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, calories, unit);
    }
}
