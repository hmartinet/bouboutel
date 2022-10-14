/*
 * Ticket.java
 *
 * Created on October 2, 2005, 1:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.files.Data;
import ch.poudriere.bouboutel.utils.Preferences;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;

/**
 *
 * @author sdperret
 */
public class Ticket extends AbstractModel {
    protected final static String SEQUENCE_KEY = "ticket.id";
    private final Performance performance;
    private String title = "";
    private Double price = 0.0;
    private String validityComment = "";

    public Ticket(Performance performance) {
        super(SEQUENCE_KEY);
        this.performance = performance;
    }

    public Ticket(long id, Performance performance) {
        super(id, SEQUENCE_KEY);
        this.performance = performance;
    }

    /**
     * @return the performance
     */
    public Performance getPerformance() {
        return performance;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the validityComment
     */
    public String getValidityComment() {
        return validityComment;
    }

    /**
     * @param validityComment the validityComment to set
     */
    public void setValidityComment(String validityComment) {
        this.validityComment = validityComment;
    }

    public Data asData(Enum command) {
        return new Data(command,
                getId(), LocalDateTime.now(), getTitle(), getPrice(),
                getValidityComment(), performance.getNbFreeSeats());
    }

    public static Ticket fromData(Data data, Performance performance) {
        Iterator<String> it = data.iterator();
        Ticket ticket = new Ticket(Long.parseLong(it.next()), performance);
        it.next(); // date unused.
        ticket.setTitle(it.next());
        ticket.setPrice(Double.valueOf(it.next()));
        ticket.setValidityComment(it.next());
        return ticket;
    }

    public int getCode() {
        String a = getId() + "COUCOU" + getPerformance().toString();
        return (a.hashCode());
    }
    
    public static Map getPrintBaseParams() throws FileNotFoundException, IOException {        
        Map params = new HashMap();
        params.put("imageX", Preferences.getImageX());
        params.put("imageY", Preferences.getImageY());
        params.put("imageScale", Preferences.getImageScale());
        params.put("showX", Preferences.getShowX());
        params.put("showY", Preferences.getShowY());
        params.put("priceX", Preferences.getPriceX());
        params.put("priceY", Preferences.getPriceY());
        params.put("oneEntryX", Preferences.getOneEntryX());
        params.put("oneEntryY", Preferences.getOneEntryY());
        params.put("image", Preferences.getTicketImage());
        return params;
    }
    
    public Map getPrintParams() throws FileNotFoundException, IOException {        
        Map params = getPrintBaseParams();
        params.put("type", "pdf");
        params.put("select", 0);
        params.put("id", this.getId());
        params.put("show", this.getPerformance().getShow().getTitle());
        params.put("company", this.getPerformance().getShow().getCompany().getName());
        params.put("room", this.getPerformance().getRoom().getName());
        params.put("street", this.getPerformance().getRoom().getStreet());
        params.put("city", this.getPerformance().getRoom().getCity());
        params.put("date", I18n.formatDate(this.getPerformance().getDate()));
        params.put("title", this.getTitle());
        params.put("price", I18n.formatCurrency(this.getPrice()));
        params.put("comment", this.getValidityComment());
        return params;
    }
    
    public static Map getPreviewParams() throws FileNotFoundException, IOException {
        Map params = getPrintBaseParams();
        params.put("type", "preview");
        params.put("select", 0);
        params.put("id", "FACTICE");
        params.put("show", "Spéctacle factice");
        params.put("company", "Compagnie exemple");
        params.put("room", "La Poudrière");
        params.put("street", "Quai Philippe-Godet 22");
        params.put("city", "Neuchâtel");
        params.put("date", I18n.formatDate());
        params.put("title", "Plein tarif");
        params.put("price", "CHF 35,00");
        params.put("comment", "Commentaire de validité");
        return params;
    }
    
    public static Map getPrintPreviewParams() throws FileNotFoundException, IOException {
        Map params = getPreviewParams();
        params.put("type", "pdf");
        return params;
    }
    
    public static Map getEmptyPrintParams() throws FileNotFoundException, IOException {
        Map params = getPrintBaseParams();
        params.put("type", "empty");
        params.put("select", 0);
        params.put("id", "");
        params.put("show", "");
        params.put("company", "");
        params.put("room", "");
        params.put("street", "");
        params.put("city", "");
        params.put("date", "");
        params.put("title", "");
        params.put("price", "");
        params.put("comment", "");
        return params;
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
