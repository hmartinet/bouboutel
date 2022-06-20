/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.utils.Preferences;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Hervé Martinet
 */
public class DataFileReader extends BufferedReader {
    private Version version;

    public DataFileReader(File file, String name)
            throws UnsupportedEncodingException, FileNotFoundException,
            IOException {
        super(new InputStreamReader(
                new FileInputStream(file.getPath()), Preferences.
                        getFileCharset()));
        System.out.println("OPEN file %s for reading".formatted(file.
                getAbsolutePath()));
        String s = readLine();
        if (s == null) {
            throw new IOException("Empty file");
        }
        if (s.startsWith("%s ".formatted(name))) {
            version = new Version(s.substring(name.length() + 1));
        } else {
            version = new Version("1.0");
        }
    }

    public Version getVersion() {
        return version;
    }
}
