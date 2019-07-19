package com.example.tscsample;

public class BlutoothList {

    String name;
    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BlutoothList{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
