/*
 * PriceNotFoundException.java
 *
 * Created on October 26, 2005, 11:19 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Booking;

/**
 *
 * @author S.D. Perret
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
@SuppressWarnings("serial")
public class PriceNotFoundException extends Exception {
    /**
     * Creates a new instance of PerformanceNotFoundException
     * @param b
     */
    public PriceNotFoundException(Booking b) {
        super("Price " + b + " not found in performance ");
    }

    public PriceNotFoundException() {
        super("Price not found in booking system ");
    }
}
