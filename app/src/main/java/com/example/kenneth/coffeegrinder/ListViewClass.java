package com.example.kenneth.coffeegrinder;

/**
 * Created by Kenneth on 21-04-2015.
 */
public class ListViewClass{
    private String name;
    private String description;
    private int id;

    public ListViewClass(String name, String description, int id){
        this.name = name;
        this.description = description;
        this.id = id;
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

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }
}
