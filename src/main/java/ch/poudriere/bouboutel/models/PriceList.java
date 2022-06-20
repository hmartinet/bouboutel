/*
 * PriceList.java
 *
 * Created on September 8, 2005, 2:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.files.Data;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class PriceList extends AbstractModel implements Comparable<PriceList> {
    protected final static String SEQUENCE_KEY = "pricelist.id";
    private String name = "";
    private final ModelList<Price> prices = new ModelList<>();

    public PriceList() {
        super(SEQUENCE_KEY);
    }

    public PriceList(long id) {
        super(id, SEQUENCE_KEY);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public ModelList<Price> getPrices() {
        return prices;
    }

    public void setPrices(ModelList<Price> prices) throws IOException {
        this.prices.clear();
        for (Price p : prices) {
            this.prices.add(p);
        }
    }

    public Data asData(Enum command) {
        return new Data(
                command,
                getId(), getName());
    }

    public static PriceList fromData(Data data) throws IOException {
        Iterator<String> it = data.iterator();
        PriceList priceList = new PriceList(Long.parseLong(it.next()));
        priceList.name = it.next();
        return priceList;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(PriceList o) {
        if (getName().equals(o.getName())) {
            return getId().compareTo(o.getId());
        }
        return getName().compareTo(o.getName());
    }
}
