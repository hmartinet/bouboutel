/*
 * PerformanceBeingEditedException.java
 *
 * Created on October 13, 2005, 11:51 AM
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Performance;

/**
 *
 * @author sdperret
 * @author herve.martinet@gmail.com
 */
public class PerformanceBeingEditedException extends Exception {
    /**
     * Creates a new instance of PerformanceFullException
     */
    public PerformanceBeingEditedException(Performance p) {
        super(p + " is currently being edited try again later");
    }
}
