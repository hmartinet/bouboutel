/*
 * Price.java
 *
 * Created on September 29, 2005, 9:55 AM
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.files.Data;
import java.util.Iterator;

/**
 *
 * @author sdperret
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class Price extends AbstractModel {
    protected final static String SEQUENCE_KEY = "price.id";
    private Action action = Price.Action.BOOKING;
    private String title = "";
    private Double price = 0.0;
    private String validityComment = "";

    public Price() {
        super(SEQUENCE_KEY);
    }

    public Price(long id) {
        super(id, SEQUENCE_KEY);
    }

    public Price(long id, Action action, String title, Double price,
            String validityComment) {
        super(id, SEQUENCE_KEY);
        this.action = action;
        this.title = title;
        this.price = price;
        this.validityComment = validityComment;
    }

    public Price(Action action, String title, Double price,
            String validityComment) {
        super(SEQUENCE_KEY);
        this.action = action;
        this.title = title;
        this.price = price;
        this.validityComment = validityComment;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Action action) {
        this.action = action;
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

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Price clone() {
        Price p;
        try {
            p = (Price) super.clone();
        } catch (CloneNotSupportedException ex) {
            p = new Price(getId());
            p.setAction(action);
            p.setPrice(price);
            p.setTitle(title);
            p.setValidityComment(validityComment);
        }
        return p;
    }

    public Price duplicate() {
        Price p = new Price();
        p.setAction(action);
        p.setPrice(price);
        p.setTitle(title);
        p.setValidityComment(validityComment);
        return p;
    }

    public Data asData(Enum command) {
        return new Data(command,
                getId(), getAction(), getTitle(), getPrice(),
                getValidityComment());
    }

    public static Price fromData(Data data) {
        Price price = new Price(Long.parseLong(data.get(0)));
        price.fromDataUpdate(data);
        return price;
    }

    public void fromDataUpdate(Data data) {
        Iterator<String> it = data.iterator();
        Long id = Long.valueOf(it.next());
        if (!getId().equals(id)) {
            throw new Error("ID %d != %d for price.fromData".formatted(
                    getId(), id));
        }
        action = Price.Action.valueOf(it.next());
        title = it.next();
        price = Double.valueOf(it.next());
        validityComment = it.next();
    }

    public enum Action {
        BOOKING, TICKET
    }
}
