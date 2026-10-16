/*
 * RoomNotFoundException.java
 *
 * Created on October 16, 2005, 12:49 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Room;

/**
 *
 * @author S.D. Perret
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
@SuppressWarnings("serial")
public class RoomNotFoundException extends Exception {    
    /**
     * Creates a new instance of PerformanceNotFoundException
     * @param p
     */
    public RoomNotFoundException(Room p) {
        super("Room " + p + " not found in booking system ");
    }
}
