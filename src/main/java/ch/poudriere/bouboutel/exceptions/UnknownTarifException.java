/*
 * UnknownTarifException.java
 *
 * Created on October 6, 2005, 9:16 AM
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
public class UnknownTarifException extends Exception {
    /**
     * Creates a new instance of UnknownTarifException
     */
    public UnknownTarifException(String aTarifName, Performance p) {
        super("Tarif " + aTarifName + " is unknown in " + p
                + " available Tarifs are " + p.getPrices());
    }
}
