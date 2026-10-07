/*
 * PerformanceFullException.java
 *
 * Created on September 8, 2005, 4:04 AM
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Performance;

/**
 *
 * @author S.D. Perret
 * @author herve.martinet@gmail.com
 */
@SuppressWarnings("serial")
public class PerformanceFullException extends Exception {
    /**
     * Creates a new instance of PerformanceFullException
     * @param p
     */
    public PerformanceFullException(Performance p) {
        super(p + " has only " + p.getNbFreeSeats() + " seats available ");
    }
}
