/*
 * PerformanceNotFoundException.java
 *
 * Created on October 13, 2005, 6:21 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Performance;

/**
 *
 * @author sdperret
 */
public class PerformanceNotFoundException extends Exception {
    /**
     * Creates a new instance of PerformanceNotFoundException
     */
    public PerformanceNotFoundException(Performance p) {
        super("Performance " + p + " not found in booking system ");
    }
}
