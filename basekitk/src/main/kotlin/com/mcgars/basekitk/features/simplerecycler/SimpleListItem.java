package com.mcgars.basekitk.features.simplerecycler;

/**
 * Created by gars on 05.01.2017.
 */

public class SimpleListItem {
    private String title;
    private String description;

    public SimpleListItem() {}
    public SimpleListItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
