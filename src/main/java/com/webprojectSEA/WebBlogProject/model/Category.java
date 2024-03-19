package com.webprojectSEA.WebBlogProject.model;

public enum Category {


    SPORT("Sport"),
    TECHNOLOGY("Technology"),
    GAMES("Games"),
    BOOKS("Books"),
    SONGS("Songs");



    private final String categoryNames;

    Category(String categoryNames){
        this.categoryNames = categoryNames;
    }


    public String getCategoryNames(){
        return categoryNames;
    }


}
