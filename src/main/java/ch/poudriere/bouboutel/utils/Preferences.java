/*
 * Preferrences.java
 *
 * Created on October 20, 2005, 10:27 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.utils;

import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.files.BackupFile;
import ch.poudriere.bouboutel.files.Version;
import ch.poudriere.bouboutel.models.Sequences;
import com.google.common.io.BaseEncoding;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;

/**
 *
 * @author sdperret
 */
public class Preferences {
    public static final int DEFAULT_IMAGE_X = 0;
    public static final int DEFAULT_IMAGE_Y = 0;
    public static final int DEFAULT_IMAGE_SCALE = 100;
    public static final int DEFAULT_SHOW_X = 7;
    public static final int DEFAULT_SHOW_Y = 38;
    public static final int DEFAULT_PRICE_X = 164;
    public static final int DEFAULT_PRICE_Y = 107;
    public static final int DEFAULT_ONE_ENTRY_X = 7;
    public static final int DEFAULT_ONE_ENTRY_Y = 116;
    
    public static final Version VERSION = new Version("3.0");
    private static final Locale locale = Locale.forLanguageTag("fr-CH");
    private static final Currency currency = Currency.getInstance(locale);
    private static final Charset fileCharset = StandardCharsets.UTF_8;
    private static String backupDirectory = "./Backup";
    private static boolean showOldPerformances = true;
    private static boolean printTicketBackground = true;
    private static final File ticketImageFile = new File("defaultTicket.jpg");
    private static String ticketImage = null;
    private static String password = "";
    private static int imageX = DEFAULT_IMAGE_X;
    private static int imageY = DEFAULT_IMAGE_Y;
    private static int imageScale = DEFAULT_IMAGE_SCALE;
    private static int showX = DEFAULT_SHOW_X;
    private static int showY = DEFAULT_SHOW_Y;
    private static int priceX = DEFAULT_PRICE_X;
    private static int priceY = DEFAULT_PRICE_Y;
    private static int oneEntryX = DEFAULT_ONE_ENTRY_X;
    private static int oneEntryY = DEFAULT_ONE_ENTRY_Y;
    
    
    /**
     * @return the locale
     */
    public static Locale getLocale() {
        return locale;
    }

    /**
     * @return the fileCharset
     */
    public static Charset getFileCharset() {
        return fileCharset;
    }

    /**
     * @return the backupDirectory
     */
    public static String getBackupDirectory() {
        return backupDirectory;
    }

    /**
     * @param aBackupDirectory the backupDirectory to set
     */
    public static void setBackupDirectory(String aBackupDirectory) {
        backupDirectory = aBackupDirectory;
    }

    /**
     * @return the showOldPerformances
     */
    public static boolean isShowOldPerformances() {
        return showOldPerformances;
    }

    /**
     * @param aShowOldPerformances the showOldPerformances to set
     */
    public static void setShowOldPerformances(boolean aShowOldPerformances) {
        showOldPerformances = aShowOldPerformances;
    }

    /**
     * @return the printTicketBackground
     */
    public static boolean isPrintTicketBackground() {
        return printTicketBackground;
    }

    /**
     * @param aPrintTicketBackground the printTicketBackground to set
     */
    public static void setPrintTicketBackground(boolean aPrintTicketBackground) {
        printTicketBackground = aPrintTicketBackground;
    }

    /**
     * @return the ticketImage
     */
    public static String getTicketImage() {
        return ticketImage;
    }

    /**
     * @return the currency
     */
    public static Currency getCurrency() {
        return currency;
    }

    /**
     * @return the password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * @param aPassword the password to set
     */
    public static void setPassword(String aPassword) {
        password = aPassword;
    }

    /**
     * @return the imageX
     */
    public static int getImageX() {
        return imageX;
    }

    /**
     * @param aImageX the imageX to set
     */
    public static void setImageX(int aImageX) {
        imageX = aImageX;
    }

    /**
     * @return the imageY
     */
    public static int getImageY() {
        return imageY;
    }

    /**
     * @param aImageY the imageY to set
     */
    public static void setImageY(int aImageY) {
        imageY = aImageY;
    }

    /**
     * @return the imageScale
     */
    public static int getImageScale() {
        return imageScale;
    }

    /**
     * @param aImageScale the imageScale to set
     */
    public static void setImageScale(int aImageScale) {
        imageScale = aImageScale;
    }

    /**
     * @return the showX
     */
    public static int getShowX() {
        return showX;
    }

    /**
     * @param aShowX the showX to set
     */
    public static void setShowX(int aShowX) {
        showX = aShowX;
    }

    /**
     * @return the showY
     */
    public static int getShowY() {
        return showY;
    }

    /**
     * @param aShowY the showY to set
     */
    public static void setShowY(int aShowY) {
        showY = aShowY;
    }

    /**
     * @return the priceX
     */
    public static int getPriceX() {
        return priceX;
    }

    /**
     * @param aPriceX the priceX to set
     */
    public static void setPriceX(int aPriceX) {
        priceX = aPriceX;
    }

    /**
     * @return the priceY
     */
    public static int getPriceY() {
        return priceY;
    }

    /**
     * @param aPriceY the priceY to set
     */
    public static void setPriceY(int aPriceY) {
        priceY = aPriceY;
    }

    /**
     * @return the oneEntryX
     */
    public static int getOneEntryX() {
        return oneEntryX;
    }

    /**
     * @param aOneEntryX the oneEntryX to set
     */
    public static void setOneEntryX(int aOneEntryX) {
        oneEntryX = aOneEntryX;
    }

    /**
     * @return the oneEntryY
     */
    public static int getOneEntryY() {
        return oneEntryY;
    }

    /**
     * @param aOneEntryY the oneEntryY to set
     */
    public static void setOneEntryY(int aOneEntryY) {
        oneEntryY = aOneEntryY;
    }

    public static String isoFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime dateFromIsoFormat(String s) {
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    public static void loadTicketImage() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(new FileInputStream(ticketImageFile))
                    .scale(((double)getImageScale()) / 100.)
                    .outputFormat("JPEG")
                    .outputQuality(1.)
                    .toOutputStream(outputStream);
            ticketImage = BaseEncoding.base64().encode(outputStream.toByteArray());
        } catch (IOException ex) {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void load() throws IOException, BackupAccessException {
        Sequences.load();
        File file = new File("bouboutel.properties");
        if (!file.exists()) {
            read_from_1_0();
            return;
        }
        Properties prop = new Properties();
        try ( InputStream in = new FileInputStream(file)) {
            prop.load(in);
            setBackupDirectory(prop.getProperty(
                    "bouboutel.backupDirectory",
                    getBackupDirectory()));
            setShowOldPerformances(Boolean.parseBoolean(prop.getProperty(
                    "bouboutel.showOldPerformances",
                    Boolean.toString(isShowOldPerformances()))));
            setPrintTicketBackground(Boolean.parseBoolean(prop.getProperty(
                    "bouboutel.printTicketBackground",
                    Boolean.toString(isPrintTicketBackground()))));
            setPassword(prop.getProperty("bouboutel.password", getPassword()));
            setImageX(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.image.x",
                    Integer.toString(getImageX()))));
            setImageY(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.image.y",
                    Integer.toString(getImageY()))));
            setImageScale(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.image.scale",
                    Integer.toString(getImageScale()))));
            setShowX(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.show.x",
                    Integer.toString(getShowX()))));
            setShowY(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.show.y",
                    Integer.toString(getShowY()))));
            setPriceX(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.price.x",
                    Integer.toString(getPriceX()))));
            setPriceY(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.price.y",
                    Integer.toString(getPriceY()))));
            setOneEntryX(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.one-entry.x",
                    Integer.toString(getOneEntryX()))));
            setOneEntryY(Integer.parseInt(prop.getProperty(
                    "bouboutel.ticket.one-entry.y",
                    Integer.toString(getOneEntryY()))));
        }
        loadTicketImage();
    }

    public static void save() throws IOException, BackupAccessException {
        BackupFile file = new BackupFile("bouboutel.properties");
        if (!file.exists()) {
            file.createNewFile();
        }
        Properties prop = new Properties();
        prop.setProperty("bouboutel.backupDirectory", getBackupDirectory());
        prop.setProperty("bouboutel.showOldPerformances",
                Boolean.toString(isShowOldPerformances()));
        prop.setProperty("bouboutel.printTicketBackground",
                Boolean.toString(isPrintTicketBackground()));
        prop.setProperty("bouboutel.backupDirectory", getBackupDirectory());
        prop.setProperty("bouboutel.password", getPassword());
        prop.setProperty("bouboutel.ticket.image.x", Integer.toString(getImageX()));
        prop.setProperty("bouboutel.ticket.image.y", Integer.toString(getImageY()));
        prop.setProperty("bouboutel.ticket.image.scale", Integer.toString(getImageScale()));
        prop.setProperty("bouboutel.ticket.show.x", Integer.toString(getShowX()));
        prop.setProperty("bouboutel.ticket.show.y", Integer.toString(getShowY()));
        prop.setProperty("bouboutel.ticket.price.x", Integer.toString(getPriceX()));
        prop.setProperty("bouboutel.ticket.price.y", Integer.toString(getPriceY()));
        prop.setProperty("bouboutel.ticket.one-entry.x", Integer.toString(getOneEntryX()));
        prop.setProperty("bouboutel.ticket.one-entry.y", Integer.toString(getOneEntryY()));
        try ( OutputStream out = new FileOutputStream(file)) {
            prop.store(out, null);
        }
        file.backup();
        Sequences.save();
    }

    public static void read_from_1_0() throws IOException, BackupAccessException {
        File file = new File("bouboutel.prefs");
        if (file.exists()) {
            try ( BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file.getPath()), "UTF-8"))) {
                reader.readLine();
                setBackupDirectory(reader.readLine());
                reader.readLine();
                setShowOldPerformances(Boolean.parseBoolean(reader.readLine()));
                setPrintTicketBackground(Boolean.parseBoolean(reader.readLine()));
                setPassword("bouboutel");
            }
            save();
            file.delete();
        }
    }
}
