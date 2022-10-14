/*
 * BookingNotFoundException.java
 *
 * Created on October 20, 2005, 11:45 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Booking;

/**
 *
 * @author sdperret
 */
public class BookingNotFoundException extends Exception {
    /**
     * Creates a new instance of PerformanceNotFoundException
     */
    public BookingNotFoundException(Booking b) {
        super("Booking " + b + " not found in booking system ");
    }

    public BookingNotFoundException() {
        super("Booking not found in booking system ");
    }
}
