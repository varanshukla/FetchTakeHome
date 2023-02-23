package com.example.fetchtakehome;

public class Item {
    int id;
    String name;
    int listId;

    public Item(int id, String name, int listId) {
        this.id = id;
        this.name = name;
        this.listId = listId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getListId() {
        return listId;
    }
}
