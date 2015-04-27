package com.example.kenneth.coffeegrinder;

/**
 * Created by Kenneth on 21-04-2015.
 */
public class ListViewClass{
    private String name;
    private String description;
    private String intentAction;

    public ListViewClass(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getIntentAction(){
        return intentAction;
    }

    public void setIntentAction(String intentAction){
        this.intentAction = intentAction;
    }
}
