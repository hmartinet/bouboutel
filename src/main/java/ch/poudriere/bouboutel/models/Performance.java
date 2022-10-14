/*
 * Performance.java
 *
 * Created on September 8, 2005, 2:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.files.BackupFile;
import ch.poudriere.bouboutel.files.BookingFile;
import ch.poudriere.bouboutel.files.Data;
import ch.poudriere.bouboutel.files.DataFileWriter;
import ch.poudriere.bouboutel.utils.Preferences;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author sdperret this class implements the notion of a performance, a show
 * presented at a given time in a given place
 */
public class Performance extends AbstractModel {
    protected final static String SEQUENCE_KEY = "performance.id";
    private Show show = null;
    private Room room = null;
    private LocalDateTime date = null;
    private Integer nbTotalSeats = 0;
    private final ModelList<Price> prices = new ModelList<>();
    private final ModelList<Booking> bookings = new ModelList<>();
    private final ModelList<Ticket> tickets = new ModelList<>();

    public Performance() {
        super(SEQUENCE_KEY);
    }

    public Performance(long id) {
        super(id, SEQUENCE_KEY);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) throws IOException,
            BackupAccessException {
        if (!Objects.equals(this.date, date)) {
            this.date = date;
            updateDataFile(getDateData());
        }
    }

    public void restoreDate(LocalDateTime date) {
        this.date = date;
    }

    private Data getDateData() {
        return new Data(BookingFile.Command.SETDATE, getDate());
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) throws IOException, BackupAccessException {
        if (!Objects.equals(this.room, room)) {
            this.room = room;
            updateDataFile(getRoomData());
        }
    }

    public void restoreRoom(Room room) {
        this.room = room;
    }

    private Data getRoomData() {
        return new Data(BookingFile.Command.SETROOM,
                getRoom().getId(), getRoom().getName());
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) throws IOException, BackupAccessException {
        if (!Objects.equals(this.show, show)) {
            this.show = show;
            updateDataFile(getShowData());
        }
    }

    public void restoreShow(Show show) {
        this.show = show;
    }

    private Data getShowData() {
        return new Data(BookingFile.Command.SETSHOW,
                getShow().getId(), getShow().getTitle());
    }

    public Integer getNbTotalSeats() {
        return nbTotalSeats;
    }

    public void setNbTotalSeats(Integer nbTotalSeats) throws IOException,
            BackupAccessException {
        if (!Objects.equals(this.nbTotalSeats, nbTotalSeats)) {
            this.nbTotalSeats = nbTotalSeats;
            updateDataFile(getNbTotalSeatsData());
        }
    }

    public void restoreNbTotalSeats(Integer nbTotalSeats) {
        this.nbTotalSeats = nbTotalSeats;
    }

    private Data getNbTotalSeatsData() {
        return new Data(BookingFile.Command.SETTOTALSEATS, getNbTotalSeats());
    }

    public ModelList<Price> getPrices() {
        return prices;
    }

    public void setPrices(ModelList<Price> prices) throws IOException,
            BackupAccessException {
        List<Data> datas = new LinkedList<>();
        for (Price p : prices) {
            Price old = this.prices.get(p.getId());
            if (old == null) {
                datas.add(p.asData(BookingFile.Command.ADDPRICE));
            } else if (!p.asData(BookingFile.Command.UPDATEPRICE).toString().
                    equals(old.asData(BookingFile.Command.UPDATEPRICE).
                            toString())) {
                datas.add(p.asData(BookingFile.Command.UPDATEPRICE));
            }
        }
        for (Price old : this.prices) {
            if (prices.get(old.getId()) == null) {
                datas.add(old.asData(BookingFile.Command.DELETEPRICE));
            }
        }
        if (!datas.isEmpty()) {
            this.prices.clear();
            for (Price p : prices) {
                this.prices.add(p);
            }
            updateDataFile(datas.toArray(Data[]::new));
        }
    }

    public ModelList<Booking> getBookings() {
        return bookings;
    }

    public Iterator<Booking> getBookingsSorted() {
        return bookings.stream().sorted((o1, o2) -> o1.getCustomerName().
                compareTo(o2.getCustomerName())).iterator();
    }

    public ModelList<Ticket> getTickets() {
        return tickets;
    }
    
    public Map<String, TarifSum> getTarifSums() {
        Map<String, TarifSum> result = new HashMap<>();
        for (Ticket t: getTickets()) {
            String tarif = t.getPrice() != 0 ? 
                    String.format("%s CHF %,.2f", t.getTitle(), t.getPrice()) : 
                    t.getTitle();
            if (!result.containsKey(tarif)) {
                result.put(tarif, new TarifSum());
            }
            result.get(tarif).add(t.getPrice());
        }
        return result;
    }

    public BackupFile getDataFile() {
        return new BackupFile("%d.bookings".formatted(getId()));
    }

    public File getTicketImageFile() {
        File ticketImageFile = new File("%d.jpg".formatted(getId()));
        if (!ticketImageFile.exists()) {
            ticketImageFile = new File("defaultTicket.jpg");
        }
        return ticketImageFile;
    }

    public void createDataFile() throws java.io.IOException,
            BackupAccessException {
        BackupFile dataFile = getDataFile();
        if (dataFile.exists()) {
            return;
        }
        dataFile.createNewFile();

        try ( DataFileWriter writer = new DataFileWriter(dataFile, "Bookings")) {
            writer.write(getShowData());
            writer.write(getRoomData());
            writer.write(getDateData());
            writer.write(getNbTotalSeatsData());
            for (Price p : getPrices()) {
                writer.write(p.asData(BookingFile.Command.ADDPRICE));
            }
        }
        dataFile.safeBackup();
        Sequences.save();
    }

    public void updateDataFile(Data... datas) throws IOException,
            BackupAccessException {
        BackupFile dataFile = getDataFile();
        if (!dataFile.exists()) {
            return;
        }
        try ( DataFileWriter writer = new DataFileWriter(dataFile)) {
            for (Data data : datas) {
                writer.write(data);
            }
        }
        dataFile.backup();
        Sequences.save();
    }

    public void loadDataFile() throws IOException, MigrationException,
            BackupAccessException {
        File dataFile = getDataFile();
        if (!dataFile.exists()) {
            createDataFile();
        }
        resetPerformance();

        new BookingFile(dataFile).read((Data data) -> {
            Iterator<String> it = data.iterator();
            switch ((BookingFile.Command) data.getCommand()) {
                case SETTOTALSEATS ->
                    restoreNbTotalSeats(Integer.valueOf(it.next()));
                case BOOK -> {
                    bookings.add(Booking.fromData(data, this));
                }
                case UPDATEBOOKING -> {
                    Booking b = bookings.get(Long.valueOf(data.get(0)));
                    b.fromDataUpdate(data);
                }
                case UNBOOK -> {
                    bookings.remove(Long.valueOf(data.get(0)));
                }
                case SELL -> {
                    tickets.add(Ticket.fromData(data, this));
                }
                case UNSELL -> {
                    tickets.remove(Long.valueOf(data.get(0)));
                }
                case ADDPRICE -> {
                    prices.add(Price.fromData(data));
                }
                case UPDATEPRICE -> {
                    Price p = prices.get(Long.valueOf(data.get(0)));
                    p.fromDataUpdate(data);
                }
                case DELETEPRICE -> {
                    prices.remove(Long.valueOf(data.get(0)));
                }
            }
        });
    }

    public Integer getNbBookedSeats() {
        return bookings.stream().mapToInt(Booking::getNbSeats).sum();
    }

    public Integer getNbSoldSeats() {
        return tickets.size();
    }

    public Integer getNbHandledSeats() {
        return getNbBookedSeats() + getNbSoldSeats();
    }

    public Integer getNbFreeSeats() {
        return getNbTotalSeats() - getNbHandledSeats();
    }

    public Double getTotalIncome() {
        return tickets.stream().mapToDouble(Ticket::getPrice).sum();
    }

    public void resetPerformance() {
        bookings.clear();
        tickets.clear();
        prices.clear();
    }

    public Data asData(Enum command) {
        return new Data(
                command,
                getId(), getDate(), getRoom().getId(),
                getShow().getId(), getNbTotalSeats());
    }

    public static Performance fromData(Data data) throws IOException {
        Iterator<String> it = data.iterator();
        BookingSystem bs = BookingSystem.getInstance();
        Performance performance = new Performance(Long.parseLong(it.next()));
        performance.restoreDate(Preferences.dateFromIsoFormat(it.next()));
        performance.restoreRoom(bs.getRooms().get(Long.valueOf(it.next())));
        performance.restoreShow(bs.getShows().get(Long.valueOf(it.next())));
        performance.restoreNbTotalSeats(Integer.valueOf(it.next()));
        return performance;
    }
    
    public class TarifSum {
        private int count = 0;
        private double sum = 0;

        public int getCount() {
            return count;
        }

        public double getSum() {
            return sum;
        }
        
        public void add(double price) {
            count++;
            sum += price;
        }
    }
}
