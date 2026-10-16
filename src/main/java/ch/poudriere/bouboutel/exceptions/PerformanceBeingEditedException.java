/*
 * PerformanceBeingEditedException.java
 *
 * Created on October 13, 2005, 11:51 AM
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Performance;

/**
 *
 * @author S.D. Perret
 * @author herve.martinet@gmail.com
 */
@SuppressWarnings("serial")
public class PerformanceBeingEditedException extends Exception {
    /**
     * Creates a new instance of PerformanceFullException
     * @param p
     */
    public PerformanceBeingEditedException(Performance p) {
        super(p + " is currently being edited try again later");
    }
}
