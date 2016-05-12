/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbGUI;

/**
 *
 * @author nico
 */
public class User {
    private String group;
    private int recNum;
    private int places;
    private String course;
    
    public User(String group, int recNum, int places, String course) {
        this.group = group;
        this.recNum = recNum;
        this.places = places;
        this.course = course;
    }
    
    public String getGroup() {
        return this.group;
    }
    
    public int getRecNum() {
        return this.recNum;
    }
    
    public int getPlaces() {
        return this.places;
    }
    
    public String getCourse() {
        return this.course;
    }
    
    public void updatePlaces() {
        this.places -= 1;
    }
    
}
