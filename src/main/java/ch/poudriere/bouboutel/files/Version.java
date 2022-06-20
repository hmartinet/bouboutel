/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.files;

/**
 *
 * @author Hervé Martinet
 */
public class Version implements Comparable<Version> {
    private final Integer primary;
    private final Integer secondary;
    
    public Version(String value) {
        String[] list = value.split("\\.");
        primary = Integer.valueOf(list[0]);
        if (list.length > 1) {
            secondary = Integer.valueOf(list[1]);
        } else {
            secondary = 0;
        }
    }

    @Override
    public int compareTo(Version o) {
        int c = primary.compareTo(o.primary);
        if (c == 0) {
            return secondary.compareTo(o.secondary);
        }
        return c;
    }
    
    public boolean lesserThan(String o) {
        return lesserThan(new Version(o));
    }
    
    public boolean lesserThan(Version v) {
        return compareTo(v) < 0;
    }
    
    @Override
    public String toString() {
        return "%s.%s".formatted(primary, secondary);
    }
}
