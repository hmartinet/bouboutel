/*
 * Room.java
 *
 * Created on September 8, 2005, 2:21 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.files.Data;
import java.util.Iterator;

/**
 *
 * @author sdperret
 */
public class Room extends AbstractModel implements Comparable<Room> {
    protected final static String SEQUENCE_KEY = "room.id";
    private String name = "";
    private String street = "";
    private String city = "";
    private Integer nbDefaultSeats = 0;

    public Room() {
        super(SEQUENCE_KEY);
    }

    public Room(long id) {
        super(id, SEQUENCE_KEY);
    }

    public Room(long id, String name, int nbDefaultSeats) {
        this(id);
        this.name = name;
    }

    /**
     * @return the fullName
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the nbDefaultSeats
     */
    public Integer getNbDefaultSeats() {
        return nbDefaultSeats;
    }

    /**
     * @param nbDefaultSeats the nbDefaultSeats to set
     */
    public void setNbDefaultSeats(Integer nbDefaultSeats) {
        this.nbDefaultSeats = nbDefaultSeats;
    }

    public Data asData(Enum command) {
        return new Data(
                command, getId(), getName(), getStreet(), getCity(),
                getNbDefaultSeats());
    }

    public static Room fromData(Data data) {
        Iterator<String> it = data.iterator();
        Room room = new Room(Long.parseLong(it.next()));
        room.setName(it.next());
        room.setStreet(it.next());
        room.setCity(it.next());
        room.setNbDefaultSeats(Integer.valueOf(it.next()));
        return room;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Room o) {
        if (getName().equals(o.getName())) {
            return getId().compareTo(o.getId());
        }
        return getName().compareTo(o.getName());
    }
}
