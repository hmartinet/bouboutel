/*
 * AbstractColumnShowable.java
 *
 * Created on October 10, 2005, 8:46 AM
 */
package ch.poudriere.bouboutel.models;

/**
 *
 * @author sdperret
 * @author hmartinet
 */
public abstract class AbstractModel {
    private long id;

    public AbstractModel(long id, String sequenceKey) {
        this.id = id;
        Sequences.update(sequenceKey, id);
    }

    public AbstractModel(String sequenceKey) {
        this.id = Sequences.next(sequenceKey);
    }

    public Long getId() {
        return id;
    }

    public void setId(long aId) {
        id = aId;
    }
}
