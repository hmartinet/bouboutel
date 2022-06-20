/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.exceptions;

import java.io.File;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class FileWriteException extends Exception {
    private File file;

    public FileWriteException(File file) {
        this.file = file;
    }
}
