/*
 * ShowNotFoundException.java
 *
 * Created on October 16, 2005, 12:44 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Show;

/**
 *
 * @author sdperret
 */
public class ShowNotFoundException extends Exception {
    /**
     * Creates a new instance of PerformanceNotFoundException
     */
    public ShowNotFoundException(Show p) {
        super("Show " + p + " not found in booking system ");
    }
}
