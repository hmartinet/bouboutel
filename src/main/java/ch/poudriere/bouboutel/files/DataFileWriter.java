/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.utils.Preferences;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Hervé Martinet
 */
public class DataFileWriter extends BufferedWriter {
    public DataFileWriter(File file)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        super(new FileWriter(file, Preferences.getFileCharset(), true));
    }
    
    public DataFileWriter(File file, boolean append)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        super(new FileWriter(file, Preferences.getFileCharset(), append));
    }

    public DataFileWriter(File file, String name, Version version)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        super(new FileWriter(file, Preferences.getFileCharset(), false));
        writeHeader(name, version);
    }

    public DataFileWriter(File file, String name)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        super(new FileWriter(file, Preferences.getFileCharset(), false));
        writeHeader(name, Preferences.VERSION);
    }

    public final void writeHeader(String name, Version version) throws
            IOException {
        write("%s %s\n".formatted(name, version));
    }

    public void write(Data data) throws IOException {
        write(data.toString());
    }
}
