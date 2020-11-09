package menu.g1;

import menu.sim.*;

import java.util.*;

public class PreferenceTracker implements Cloneable {
    public FamilyMember member;
    public Map<Food.FoodType, Integer> lastServed;
    public Map<Food.FoodType, Double[]> satisfactionsForWeek;

    public PreferenceTracker(FamilyMember member) {
        this.member = member;
        initLastServed();
        this.satisfactionsForWeek = new HashMap<>();
        generateSatisfactionsForWeek();
    }

    public PreferenceTracker(FamilyMember member,
                             Map<Food.FoodType, Integer> lastServed,
                             Map<Food.FoodType, Double[]> satisfactionsForWeek) {
        this.member = member;
        this.lastServed = lastServed;
        this.satisfactionsForWeek = new HashMap<>(satisfactionsForWeek);
    }

    public PreferenceTracker(PreferenceTracker prefTracker) {
        this.member = prefTracker.member;
        this.lastServed = prefTracker.lastServed;
        this.satisfactionsForWeek = new HashMap<>(prefTracker.satisfactionsForWeek);
    }

    private void initLastServed() {
        lastServed = new HashMap<>();
        for (Food.FoodType lunch: Food.getFoodTypes(Food.MealType.LUNCH)) {
            lastServed.put(lunch, 0);
        }

        for (Food.FoodType dinner: Food.getFoodTypes(Food.MealType.DINNER)) {
            lastServed.put(dinner, 0);
        }
    }

    public void update(Integer week, MealHistory mealHistory) {
        updateLastServed(week, mealHistory);
        generateSatisfactionsForWeek();
    }

    private void updateLastServed(Integer week, MealHistory mealHistory) {
        if (week > 1) {
            for (Food.FoodType lunch: Food.getFoodTypes(Food.MealType.LUNCH)) {
                Integer lunchLastServed = lastServed.get(lunch);
                if (lunchLastServed > 0) {
                    lastServed.put(lunch, lunchLastServed + 7);
                }
            }
            for (Food.FoodType dinner: Food.getFoodTypes(Food.MealType.DINNER)) {
                Integer dinnerLastServed = lastServed.get(dinner);
                if (dinnerLastServed > 0) {
                    lastServed.put(dinner, dinnerLastServed + 7);
                }
            }

            Map<Integer, Map<MemberName, Map<Food.MealType, Food.FoodType>>> dailyMeals = mealHistory.getDailyFamilyMeals();
            for (int daysAgo = 7; daysAgo > 0; daysAgo--) {
                Integer day = (7 * (week - 1)) - daysAgo + 1;
                Food.FoodType lastLunch = dailyMeals.get(day).get(member.getName()).get(Food.MealType.LUNCH);
                Food.FoodType lastDinner = dailyMeals.get(day).get(member.getName()).get(Food.MealType.DINNER);

                if (lastLunch != null) {
                    lastServed.put(lastLunch, daysAgo);
                }
                if (lastDinner != null) {
                    lastServed.put(lastDinner, daysAgo);
                }
            }
        }
    }

    private void generateSatisfactionsForWeek() {
        for (Map.Entry<Food.FoodType, Integer> lastServedFood: lastServed.entrySet()) {
            Food.FoodType food = lastServedFood.getKey();
            Integer daysAgo = lastServedFood.getValue();
            Double pref = member.getFoodPreference(food);

            Double[] prefForNextWeek = new Double[7];

            for (int i = 0; i < 7; i++) {
                if (daysAgo == 0) {
                    prefForNextWeek[i] = pref;
                }
                else {
                    prefForNextWeek[i] = pref * ((double) (daysAgo + i)/(daysAgo + i + 1));
                }
            }
            satisfactionsForWeek.put(food, prefForNextWeek);
        }
    }

    public void addTempFood(Food.FoodType food, int day) {
        if (food != null) {
            Double[] satForWeek = satisfactionsForWeek.get(food);
            Double daysAgo = 1.0;
            Double pref = member.getFoodPreference(food);
            for (int i = day + 1; i < satForWeek.length; i++) {
                satForWeek[i] = pref * (daysAgo/(daysAgo + 1.0));
                daysAgo += 1.0;
            }
        }
    }

    public void reset() {
        generateSatisfactionsForWeek();
    }

    @Override
    public String toString() {
        String str = "";
        if (satisfactionsForWeek != null) {
            for (Map.Entry<Food.FoodType, Double[]> satisfactionEntry : satisfactionsForWeek.entrySet()) {
                str += satisfactionEntry.getKey().toString() + ": ";
                for (Double sat : satisfactionEntry.getValue()) {
                    str += sat.toString() + " ";
                }
                str += "\n";
            }
            return  str;
        }
        return "SATISFACTIONS ARE NULL";
    }
}
