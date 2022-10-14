/*
 * CompanyNotFoundException.java
 *
 * Created on October 16, 2005, 12:39 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.exceptions;

import ch.poudriere.bouboutel.models.Company;

/**
 *
 * @author sdperret
 */
public class CompanyNotFoundException extends Exception {
    public CompanyNotFoundException(Company c) {
        super("Company " + c + " not found in booking system ");
    }
}
