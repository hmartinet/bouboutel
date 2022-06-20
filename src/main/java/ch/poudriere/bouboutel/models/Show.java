/*
 * Show.java
 *
 * Created on September 8, 2005, 2:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.files.Data;
import java.util.Iterator;

/**
 *
 * @author sdperret this class implements the notion of a show. it is NOT
 * associated with a time and location but can be played in different
 * performances.
 */
public class Show extends AbstractModel implements Comparable<Show> {
    protected final static String SEQUENCE_KEY = "show.id";
    private Company company = null;
    private String title = "";
    private String comment = "";

    public Show(Company company) {
        super(SEQUENCE_KEY);
        this.company = company;
    }

    public Show(long id, Company company) {
        super(id, SEQUENCE_KEY);
        this.company = company;
    }

    /**
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(Company company) {
        this.company = company;
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
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Data asData(Enum command) {
        return new Data(command, getId(), getTitle(), getCompany().getId());
    }

    public static Show fromData(Data data) {
        Iterator<String> it = data.iterator();
        long id = Long.parseLong(it.next());
        String title = it.next();
        Company company = BookingSystem.getInstance().getCompanies().get(
                Long.valueOf(it.next()));
        Show show = new Show(id, company);
        show.setTitle(title);
        return show;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int compareTo(Show o) {
        if (getTitle().equals(o.getTitle())) {
            return getId().compareTo(o.getId());
        }
        return getTitle().compareTo(o.getTitle());
    }
}
