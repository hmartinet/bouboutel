/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.utils.FileUtil;
import ch.poudriere.bouboutel.utils.Preferences;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class BackupFile extends File {
    public BackupFile(String pathname) {
        super(pathname);
    }

    public File getBackupFile() {
        return Paths.get(Preferences.getBackupDirectory(), getName()).toFile();
    }

    private static void copy(File src, File dst) throws BackupAccessException {
        try {
            FileUtil.copy(src, dst);
        } catch (IOException ex) {
            throw new BackupAccessException();
        }
    }

    private static boolean checksumEqual(File src, File dst) throws
            BackupAccessException {
        try {
            return sha1(src).equals(sha1(dst));
        } catch (IOException ex) {
            throw new BackupAccessException();
        }
    }

    public void backup() throws BackupAccessException {
        if (!exists()) {
            return;
        }
        BackupFile.copy(this, getBackupFile());
    }

    public void safeBackup() throws BackupAccessException {
        if (!exists()) {
            return;
        }
        File backup = getBackupFile();
        if (backup.exists() && !BackupFile.checksumEqual(this, backup)) {
            int i = 0;
            while (true) {
                i++;
                File bak = new File(Preferences.getBackupDirectory(),
                        "%s.%d".formatted(getName(), i));
                if (!bak.exists()) {
                    BackupFile.copy(backup, bak);
                    break;
                }
            }
        }
        BackupFile.copy(this, backup);
    }

    public static String sha1(File file) throws IOException {
        try ( InputStream is = new FileInputStream(file)) {
            return DigestUtils.sha1Hex(is);
        }
    }
}
