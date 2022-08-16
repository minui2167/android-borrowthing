package com.minui.borrowthing.model;

import java.io.Serializable;

public class Result implements Serializable {
    private Location[] results;

    public Location[] getResults() {
        return results;
    }

    public void setResults(Location[] results) {
        this.results = results;
    }
}
