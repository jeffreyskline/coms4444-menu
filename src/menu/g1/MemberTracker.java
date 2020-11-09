package menu.g1;

import menu.sim.FamilyMember;
import menu.sim.Food;
import menu.sim.MealHistory;

public class MemberTracker implements Comparable<MemberTracker> {
    private FamilyMember member;
    public PreferenceTracker prefTracker;
    private Double avgSatisfaction;
    private Double weight;

    public MemberTracker(FamilyMember member) {
        this.member = member;
        this.prefTracker = new PreferenceTracker(member);
        this.avgSatisfaction = 0.0;
        this.weight = 1.0;
    }

    public void update(Integer week, MealHistory mealHistory, Double scale) {
        updateAvgSatisfation(week, mealHistory);
        updateWeight(scale);
        updatePrefTracker(week, mealHistory);
    }

    public Double getWeightedPreference(Food.FoodType foodType, int day) {
        return prefTracker.satisfactionsForWeek.get(foodType)[day] * weight;
    }

    public Double getAvgSatisfaction() {
        return avgSatisfaction;
    }

    private void updateAvgSatisfation(Integer week, MealHistory mealHistory) {
        if (week > 1) {
            avgSatisfaction = mealHistory.getAverageSatisfaction(week - 1, member.getName());
        }
        else {
            avgSatisfaction = 0.0;
        }
    }

    private void updateWeight(Double scale) {
        if (avgSatisfaction == 0) {
            weight = scale/0.0000001;
        }
        else {
            weight = scale/avgSatisfaction;
        }
    }

    public Double getWeight() {
        return weight;
    }

    private void updatePrefTracker(Integer week, MealHistory mealHistory) {
        prefTracker.update(week, mealHistory);
    }

    /*public PreferenceTracker getPrefTrackerCopy() {
        //return new PreferenceTracker(prefTracker);
        return prefTracker;
    }*/

    @Override
    public int compareTo(MemberTracker otherMember) {
        return this.avgSatisfaction.compareTo(otherMember.getAvgSatisfaction());
    }
}
