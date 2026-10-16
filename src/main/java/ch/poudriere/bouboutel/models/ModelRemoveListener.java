/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <T>
 */
@FunctionalInterface
public interface ModelRemoveListener<T extends AbstractModel> {
    public void removed(T model);
}
