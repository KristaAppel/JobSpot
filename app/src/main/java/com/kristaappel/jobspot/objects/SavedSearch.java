package com.kristaappel.jobspot.objects;


import java.io.Serializable;

public class SavedSearch implements Serializable{

    private final String keywords;
    private final String radius;
    private final String location;
    private final String days;
    private final String dateTime;

    public SavedSearch(String _keywords, String _radius, String _location, String _days, String _datetime){
        keywords = _keywords;
        radius = _radius;
        location = _location;
        days = _days;
        dateTime = _datetime;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getRadius() {
        return radius;
    }

    public String getLocation() {
        return location;
    }

    public String getDays() {
        return days;
    }

    public String getDateTime(){
        return dateTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SavedSearch){
            SavedSearch otherSearch = (SavedSearch) obj;
            return this.getKeywords().equals(otherSearch.getKeywords()) && this.getLocation().equals(otherSearch.getLocation()) && this.getDays().equals(otherSearch.getDays()) && this.getRadius().equals(otherSearch.getRadius());
        }
        return false;
    }

}
