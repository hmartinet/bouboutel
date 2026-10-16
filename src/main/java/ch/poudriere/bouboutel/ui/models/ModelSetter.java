/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.models.AbstractModel;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <M>
 * @param <T>
 */
@FunctionalInterface
public interface ModelSetter<M extends AbstractModel, T> {
    public void apply(M model, T value);
}
