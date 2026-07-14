package com.example.thoughtbook;

public class Shelf {
    String shelfId;
    String name;
    long dateCreated;
    int sortOrder;

    public String getShelfId(){
        return shelfId;
    }

    public String getName() {
        return name;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setShelfId(String shelfId) {
        this.shelfId = shelfId;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
