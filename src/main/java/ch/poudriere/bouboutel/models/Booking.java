/*
 * Booking.java
 *
 * Created on September 8, 2005, 3:44 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.files.Data;
import ch.poudriere.bouboutel.utils.Preferences;
import ch.poudriere.bouboutel.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.Iterator;

/**
 *
 * @author sdperret
 */
public class Booking extends AbstractPrintableModel implements
        Comparable<Booking> {
    protected final static String SEQUENCE_KEY = "booking.id";
    private final Performance performance;
    private String customerName = "";
    private String customerPhone = "";
    private Integer nbSeats = 0;
    private LocalDateTime date = LocalDateTime.now();
    private String comment = "";

    public Booking(Performance performance) {
        super(SEQUENCE_KEY);
        this.performance = performance;
    }

    public Booking(long id, Performance performance) {
        super(id, SEQUENCE_KEY);
        this.performance = performance;
    }
    // TODO: check used.
//    public void convertBookingToPrebooking() throws IOException {
//        nbSeatsSaved = getNbSeats();
//        commentSaved = getComment();
//
//        // find tarif which is not a booking and has the appropriate title
//        boolean found = false;
//        for (Price p : tarifs) {
//            if (p.getTitle().toUpperCase().equals(getComment().toUpperCase())
//                    && p.getAction() == Price.Action.TICKET) {
//                p.setNb(getNbSeats());
//                found = true;
//            }
//        }
//        if (found == false) {
//
//            // find most expensive ticket price
//            double maxPrice = 0;
//            Price maxTarif = tarifs.get(0);
//            for (Price p : tarifs) {
//                if (p.getPrice() >= maxPrice && p.getAction()
//                        == Price.Action.TICKET) {
//                    maxTarif = p;
//                    maxPrice = p.getPrice();
//                }
//            }
//            maxTarif.setNb(getNbSeats());
//
//        }
//
//        getPerformance().convertBookingToPrebooking(this);
//    }
//    public void journalBook(int aNb, String aComment) throws IOException {
//
//        File dataFile = performance.getDataFile();
//        File backupFile = performance.getBackupFile();
//
//        if (!dataFile.exists()) {
//            performance.createDataFile();
//        }
//
//        System.out.println("writing booking to " + dataFile.getPath());
//
//        BackedUpBufferedWriter writer;
//        try {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true), "UTF-8"),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true), "UTF-8")
//            );
//        } catch (java.io.UnsupportedEncodingException ee) {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true)),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true))
//            );
//        }
//
//        /*BufferedWriter backup;
//        try{
//            backup = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ), "UTF-8"));
//        }catch( java.io.UnsupportedEncodingException ee ){
//            backup = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) ));
//        }*/
//        writer.write("BOOK" + "|" + getId() + "|" + Preferrences.
//                getSaveableDateFormater().format(new Date()) + "|" + aNb + "|"
//                + getClient().getName() + "|" + aComment + "|" + (performance.
//                getNbFreeSeats()) + "|" + getClient().getStreet() + "|"
//                + getClient().getTown() + "|" + getClient().getPhone() + "|"
//                + getClient().getEmail() + "\n");
//        writer.close();
//    }
//
//    public void journalUpdateBooking(
//            int aNb, String aName, String aComment, String aPhone) throws
//            IOException {
//
//        File dataFile = performance.getDataFile();
//        File backupFile = performance.getBackupFile();
//
//        if (!dataFile.exists()) {
//            performance.createDataFile();
//        }
//
//        BackedUpBufferedWriter writer;
//        try {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true), "UTF-8"),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true), "UTF-8")
//            );
//        } catch (java.io.UnsupportedEncodingException ee) {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true)),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true))
//            );
//        }
//
//        writer.write("UPDATEBOOKING" + "|" + getId() + "|" + Preferrences.
//                getSaveableDateFormater().format(new Date()) + "|" + aNb + "|"
//                + aName + "|" + aComment + "|" + (performance.getNbFreeSeats()
//                - (aNb - getNbSeats())) + "|" + aStreet + "|" + aTown + "|"
//                + aEmail + "\n");
//        writer.close();
//    }
//
//    public void journalUnbook() throws IOException {
//
//        File dataFile = performance.getDataFile();
//        File backupFile = performance.getBackupFile();
//
//        if (!dataFile.exists()) {
//            performance.createDataFile();
//        }
//
//        BackedUpBufferedWriter writer;
//        try {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true), "UTF-8"),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true), "UTF-8")
//            );
//        } catch (java.io.UnsupportedEncodingException ee) {
//            writer = new BackedUpBufferedWriter(
//                    new OutputStreamWriter(new FileOutputStream(dataFile.
//                            getPath(), true)),
//                    new OutputStreamWriter(new FileOutputStream(backupFile.
//                            getPath(), true))
//            );
//        }
//
//        writer.write("UNBOOK" + "|" + getId() + "|" + Preferrences.
//                getSaveableDateFormater().format(new Date()) + "|"
//                + getNbSeats() + "|" + getClient().getName() + "|"
//                + (performance.getNbFreeSeats() + getNbSeats()) + "\n");
//        writer.close();
//
//    }
//    public boolean hasBookings() {
//        boolean hasBookings = false;
//        for (Price p : getTarifs()) {
//            if (p.getAction() == Price.Action.BOOKING && p.getNb() > 0) {
//                hasBookings = true;
//                return hasBookings;
//            }
//        }
//        return hasBookings;
//    }

    /**
     * @return the performance
     */
    public Performance getPerformance() {
        return performance;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @param customerName the customerName to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName.toUpperCase(Preferences.getLocale());
    }

    /**
     * @return the customerPhone
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * @param customerPhone the customerPhone to set
     */
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    /**
     * @return the nbSeats
     */
    public Integer getNbSeats() {
        return nbSeats;
    }

    /**
     * @param nbSeats the nbSeats to set
     */
    public void setNbSeats(Integer nbSeats) {
        this.nbSeats = nbSeats;
    }

    /**
     * @return the date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
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
        return new Data(command,
                getId(), getDate(), getNbSeats(), getCustomerName(),
                getCustomerPhone(), getComment(), performance.getNbFreeSeats());
    }

    public static Booking fromData(Data data, Performance performance) {
        Booking booking = new Booking(Long.parseLong(data.get(0)), performance);
        booking.fromDataUpdate(data);
        return booking;
    }

    public void fromDataUpdate(Data data) {
        Iterator<String> it = data.iterator();
        Long id = Long.valueOf(it.next());
        if (!getId().equals(id)) {
            throw new Error("ID %d != %d for price.fromData".formatted(
                    getId(), id));
        }
        date = Preferences.dateFromIsoFormat(it.next());
        nbSeats = Integer.valueOf(it.next());
        customerName = it.next();
        customerPhone = it.next();
        comment = it.next();
    }

    @Override
    public String toPrintLine() {
        return "%s %d   %s".formatted(
                StringUtils.pad(getCustomerName(), '_', " ", -35), getNbSeats(),
                getComment());
    }

    @Override
    public int compareTo(Booking o) {
        if (getDate().equals(o.getDate())) {
            return getId().compareTo(o.getId());
        }
        return getDate().compareTo(o.getDate());
    }
}
