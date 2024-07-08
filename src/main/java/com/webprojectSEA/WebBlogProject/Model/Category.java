package com.webprojectSEA.WebBlogProject.Model;

public enum Category {


    SPORT("SPORT"),
    TECHNOLOGY("TECHNOLOGY"),
    GAMES("GAMES"),
    BOOKS("BOOKS"),
    SONGS("SONGS");




    private final String categoryNames;

    Category(String categoryNames){
        this.categoryNames = categoryNames;
    }


    public String getCategoryNames(){
        return categoryNames;
    }


}
