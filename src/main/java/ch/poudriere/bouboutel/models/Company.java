/*
 * Company.java
 *
 * Created on September 8, 2005, 3:25 AM
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
 * @author sdperret
 */
public class Company extends AbstractModel implements Comparable<Company> {
    protected final static String SEQUENCE_KEY = "company.id";
    private String name = "";

    public Company() {
        super(SEQUENCE_KEY);
    }

    public Company(long id) {
        super(id, SEQUENCE_KEY);
    }

    public Company(long id, String name) {
        super(id, SEQUENCE_KEY);
        this.name = name;
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

    public Data asData(Enum command) {
        return new Data(command, getId(), getName());
    }

    public static Company fromData(Data data) {
        Iterator<String> it = data.iterator();
        Company company = new Company(Long.parseLong(it.next()));
        company.setName(it.next());
        return company;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean equals(Company o) {
        return getId().equals(o.getId());
    }

    @Override
    public int compareTo(Company o) {
        if (getName().equals(o.getName())) {
            return getId().compareTo(o.getId());
        }
        return getName().compareTo(o.getName());
    }
}
