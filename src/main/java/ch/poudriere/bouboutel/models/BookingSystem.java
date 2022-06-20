/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.files.BackupFile;
import ch.poudriere.bouboutel.files.Data;
import ch.poudriere.bouboutel.files.DataFileWriter;
import ch.poudriere.bouboutel.files.SetupFile;
import ch.poudriere.bouboutel.utils.PDFManager;
import ch.poudriere.bouboutel.utils.Preferences;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Semaphore;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class BookingSystem {
    private static volatile BookingSystem instance;

    public static BookingSystem getInstance() {
        return instance;
    }

    public static BookingSystem load() throws IOException, MigrationException,
            BackupAccessException {
        Preferences.load();
        System.out.println("*** Preferrences loaded");
        instance = new BookingSystem();
        instance.loadSetup();
        System.out.println("*** Setup loaded");
        Preferences.save();
        instance.makeBackup();
        return instance;
    }
    public final PDFManager pdfManager = new PDFManager();
    public final Semaphore ticketPrintSemaphore = new Semaphore(1, true);
    private final ModelList<Company> companies = new ModelList<>();
    private final ModelList<Room> rooms = new ModelList<>();
    private final ModelList<Show> shows = new ModelList<>();
    private final ModelList<Performance> performances = new ModelList<>();
    private final ModelList<PriceList> priceLists = new ModelList<>();
    private String lastCustomerName = "";
    private String lastCustomerPhone = "";

    private BookingSystem() {
        companies.addRemoveListener(c -> {
            shows.removeIf(s -> s.getCompany().equals(c));
        });
        rooms.addRemoveListener(r -> {
            performances.removeIf(p -> p.getRoom().equals(r));
        });
        shows.addRemoveListener(s -> {
            performances.removeIf(p -> p.getShow().equals(s));
        });
    }

    /**
     * @return the companies
     */
    public ModelList<Company> getCompanies() {
        return companies;
    }

    /**
     * @return the rooms
     */
    public ModelList<Room> getRooms() {
        return rooms;
    }

    /**
     * @return the shows
     */
    public ModelList<Show> getShows() {
        return shows;
    }

    /**
     * @return the performances
     */
    public ModelList<Performance> getPerformances() {
        return performances;
    }

    /**
     * @return the priceLists
     */
    public ModelList<PriceList> getPriceLists() {
        return priceLists;
    }

    public ModelList<Booking> getAllBookings(boolean all) {
        ModelList<Booking> ml = new ModelList<>();
        for (Performance p : performances) {
            if (all || !p.getDate().toLocalDate().isBefore(LocalDate.now())) {
                ml.addAll(p.getBookings());
            }
        }
        return ml;
    }

    /**
     * @return the lastCustomerName
     */
    public String getLastCustomerName() {
        return lastCustomerName;
    }

    /**
     * @param lastCustomerName the lastCustomerName to set
     */
    public void setLastCustomerName(String lastCustomerName) {
        this.lastCustomerName = lastCustomerName;
    }

    /**
     * @return the lastCustomerPhone
     */
    public String getLastCustomerPhone() {
        return lastCustomerPhone;
    }

    /**
     * @param lastCustomerPhone the lastCustomerPhone to set
     */
    public void setLastCustomerPhone(String lastCustomerPhone) {
        this.lastCustomerPhone = lastCustomerPhone;
    }

    private void clear() {
        companies.clear();
        rooms.clear();
        shows.clear();
        performances.clear();
    }

    public final void makeBackup() throws IOException, BackupAccessException {
        new BackupFile("bouboutel.setup").safeBackup();
        new BackupFile("bouboutel.properties").backup();
        new BackupFile("bouboutel.statics").backup();
        new BackupFile("defaultTicket.jpg").backup();
        new BackupFile("header.jpg").backup();
        for (Performance performance : performances) {
            performance.getDataFile().safeBackup();
        }
    }

    public final void loadSetup() throws MigrationException, IOException,
            BackupAccessException {
        File setupFile = new File("bouboutel.setup");
        if (!setupFile.exists()) {
            return;
        }

        clear();
        new SetupFile(setupFile).read((Data data) -> {
            switch ((SetupFile.Command) data.getCommand()) {
                case ADDPERFORMER ->
                    companies.add(Company.fromData(data));
                case ADDROOM ->
                    rooms.add(Room.fromData(data));
                case ADDSHOW ->
                    shows.add(Show.fromData(data));
                case ADDPERFORMANCE ->
                    performances.add(Performance.fromData(data));
                case ADDPRICELIST ->
                    priceLists.add(PriceList.fromData(data));
                case ADDPRICE ->
                    priceLists.getLast().getPrices().add(Price.fromData(data));
            }
        });
        for (Performance performance : performances) {
            performance.loadDataFile();
        }
    }

    public void saveSetup() throws IOException, BackupAccessException {
        BackupFile file = new BackupFile("bouboutel.setup");

        if (file.exists() && !file.canWrite()) {
            throw new IOException();
        }

        file.createNewFile();

        new SetupFile(file).write((DataFileWriter writer) -> {
            for (Company company : getCompanies()) {
                writer.write(company.asData(SetupFile.Command.ADDPERFORMER));
            }
            for (Show show : getShows()) {
                writer.write(show.asData(SetupFile.Command.ADDSHOW));
            }
            for (Room room : getRooms()) {
                writer.write(room.asData(SetupFile.Command.ADDROOM));
            }
            for (Performance performance : getPerformances()) {
                writer.write(performance.
                        asData(SetupFile.Command.ADDPERFORMANCE));
            }
            for (PriceList priceList : getPriceLists()) {
                writer.write(priceList.
                        asData(SetupFile.Command.ADDPRICELIST));
                for (Price p : priceList.getPrices()) {
                    writer.write(p.asData(SetupFile.Command.ADDPRICE));
                }
            }
        });
        file.backup();
    }

    public void exportStatistics(File file) throws IOException {
        // open statistics file for writing
        if (!file.exists()) {
            file.createNewFile();
        }
        if (file.canWrite()) {
            try ( Writer writer = new DataFileWriter(file, false)) {
                DateTimeFormatter df = DateTimeFormatter.ofPattern(
                        "dd.MM.yyyy;HH:mm");
                writer.write(LocalDateTime.now().format(df) + "\n");

                // write column labels on the second line
                writer.write(
                        "Titre;Troupe;Date;Heure;Salle;Tarif;Libres;Reservés;Vendu;Encaissé\n");
                String c = "%s;";
                String sc = "\"%s\";";

                // add a line for each performance
                for (Performance p : performances) {
                    System.out.println("saving statistics for " + p + "\n");
                    writer.write(sc.formatted(p.getShow().getTitle()));
                    writer.write(
                            sc.formatted(p.getShow().getCompany().getName()));
                    writer.write(c.formatted(p.getDate().format(df)));
                    writer.write(sc.formatted(p.getRoom().getName()));
                    writer.write(sc.formatted("TOTAL"));
                    writer.write(c.formatted(p.getNbFreeSeats()));
                    writer.write(c.formatted(p.getNbBookedSeats()));
                    writer.write(c.formatted(p.getNbSoldSeats()));
                    writer.write(c.formatted(p.getTotalIncome()));
                    writer.write("\n");
                    
                    for (Map.Entry<String, Performance.TarifSum> e : 
                           p.getTarifSums().entrySet()) {
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(""));
                        writer.write(sc.formatted(e.getKey()));
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(""));
                        writer.write(c.formatted(e.getValue().getCount()));
                        writer.write(c.formatted(e.getValue().getSum()));
                        writer.write("\n");
                   }                   
                }
            }
        } else {
            throw new IOException();
        }
    }

    public void exportStatisticsXLSX(File file) throws IOException {
        try ( XSSFWorkbook wb = new XSSFWorkbook()) {

            Sheet sheet = wb.createSheet("Persons");
            sheet.setColumnWidth(0, 6000);
            sheet.setColumnWidth(1, 4000);

            Row header = sheet.createRow(0);

            XSSFFont font = wb.createFont();
            font.setBold(true);
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.
                    setFillForegroundColor(IndexedColors.GREY_25_PERCENT.
                            getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(font);

            DataFormat format = wb.createDataFormat();
            CellStyle integerCellStyle = wb.createCellStyle();
            integerCellStyle.setDataFormat(format.getFormat("#,##0"));
            CellStyle decimalCellStyle = wb.createCellStyle();
            decimalCellStyle.setDataFormat(format.getFormat("#,##0.00"));

            CellStyle dateCellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            dateCellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("m/d/yy h:mm"));

            Cell headerCell = header.createCell(0);
            headerCell.setCellValue("Titre");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(1);
            headerCell.setCellValue("Troupe");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(2);
            headerCell.setCellValue("Date");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(3);
            headerCell.setCellValue("Salle");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(4);
            headerCell.setCellValue("Tarif");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(5);
            headerCell.setCellValue("Libres");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(6);
            headerCell.setCellValue("Reservés");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(7);
            headerCell.setCellValue("Vendu");
            headerCell.setCellStyle(headerStyle);

            headerCell = header.createCell(8);
            headerCell.setCellValue("Encaissé");
            headerCell.setCellStyle(headerStyle);

            int rindex = 0;
            for (Performance p : performances) {
                rindex++;
                Row row = sheet.createRow(rindex);
                Cell cell;

                row.createCell(0).setCellValue(p.getShow().getTitle());
                row.createCell(1).setCellValue(p.getShow().getCompany().
                        getName());

                cell = row.createCell(2);
                cell.setCellValue(p.getDate());
                cell.setCellStyle(dateCellStyle);

                row.createCell(3).setCellValue(p.getRoom().getName());
                
                row.createCell(4).setCellValue("TOTAL");

                cell = row.createCell(5);
                cell.setCellValue(p.getNbFreeSeats());
                cell.setCellStyle(integerCellStyle);

                cell = row.createCell(6);
                cell.setCellValue(p.getNbBookedSeats());
                cell.setCellStyle(integerCellStyle);

                cell = row.createCell(7);
                cell.setCellValue(p.getNbSoldSeats());
                cell.setCellStyle(integerCellStyle);

                cell = row.createCell(8);
                cell.setCellValue(p.getTotalIncome());
                cell.setCellStyle(decimalCellStyle);
                
                for (Map.Entry<String, Performance.TarifSum> e : 
                        p.getTarifSums().entrySet()) {
                    rindex++;
                    row = sheet.createRow(rindex);

                    row.createCell(4).setCellValue(e.getKey());

                    cell = row.createCell(7);
                    cell.setCellValue(e.getValue().getCount());
                    cell.setCellStyle(integerCellStyle);

                    cell = row.createCell(8);
                    cell.setCellValue(e.getValue().getSum());
                    cell.setCellStyle(decimalCellStyle);
                }
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);

            FileOutputStream outputStream = new FileOutputStream(file);
            wb.write(outputStream);
        }
    }

    public String getStatString() {
        int nbBookedSeats = 0;
        int nbSoldSeats = 0;
        double totalIncome = 0;
        for (Performance p : performances) {
            nbBookedSeats += p.getNbBookedSeats();
            nbSoldSeats += p.getNbSoldSeats();
            totalIncome += p.getTotalIncome();
        }
        return "%s\u202F: %s (%s) | %s\u202F: %s | %s\u202F: %s".formatted(
                I18n.get("label.nbSoldSeats"), nbSoldSeats,
                I18n.formatCurrency(totalIncome),
                I18n.get("label.nbBookedSeats"), nbBookedSeats,
                I18n.get("label.nbHandledSeats"), nbSoldSeats + nbBookedSeats
        );
    }
}
