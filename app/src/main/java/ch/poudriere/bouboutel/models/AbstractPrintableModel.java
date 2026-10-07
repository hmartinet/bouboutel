/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public abstract class AbstractPrintableModel extends AbstractModel {
    public AbstractPrintableModel(long id, String sequenceKey) {
        super(id, sequenceKey);
    }

    public AbstractPrintableModel(String sequenceKey) {
        super(sequenceKey);
    }

    public abstract String toPrintLine();
}
