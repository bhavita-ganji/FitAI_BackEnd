package com.fitai.dto;

public class AdminStatsDto {

    private long totalUsers;
    private long totalWorkoutsLogged;
    private long totalMealsLogged;
    private long totalExercises;
    private long totalKnowledgeDocs;
    private long activeUsersToday;

    public AdminStatsDto() {}

    public AdminStatsDto(long totalUsers, long totalWorkoutsLogged, long totalMealsLogged, long totalExercises, long totalKnowledgeDocs, long activeUsersToday) {
        this.totalUsers = totalUsers;
        this.totalWorkoutsLogged = totalWorkoutsLogged;
        this.totalMealsLogged = totalMealsLogged;
        this.totalExercises = totalExercises;
        this.totalKnowledgeDocs = totalKnowledgeDocs;
        this.activeUsersToday = activeUsersToday;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalWorkoutsLogged() { return totalWorkoutsLogged; }
    public void setTotalWorkoutsLogged(long totalWorkoutsLogged) { this.totalWorkoutsLogged = totalWorkoutsLogged; }

    public long getTotalMealsLogged() { return totalMealsLogged; }
    public void setTotalMealsLogged(long totalMealsLogged) { this.totalMealsLogged = totalMealsLogged; }

    public long getTotalExercises() { return totalExercises; }
    public void setTotalExercises(long totalExercises) { this.totalExercises = totalExercises; }

    public long getTotalKnowledgeDocs() { return totalKnowledgeDocs; }
    public void setTotalKnowledgeDocs(long totalKnowledgeDocs) { this.totalKnowledgeDocs = totalKnowledgeDocs; }

    public long getActiveUsersToday() { return activeUsersToday; }
    public void setActiveUsersToday(long activeUsersToday) { this.activeUsersToday = activeUsersToday; }
}
