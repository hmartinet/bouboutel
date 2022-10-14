/*
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.utils.Preferences;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Hervé Martinet
 */
public class BookingFile extends AbstractDataFile {
    public static enum Command {
        SETSHOW,
        SETROOM,
        SETDATE,
        SETTOTALSEATS,
        ADDPRICE,
        UPDATEPRICE,
        DELETEPRICE,
        BOOK,
        UPDATEBOOKING,
        UNBOOK,
        SELL,
        UNSELL,
        UNDEFINED,
    }

    public BookingFile(File file) {
        super(file);
    }

    @Override
    protected String getIdendifier() {
        return "Bookings";
    }

    @Override
    protected MigrationDatas migrate(MigrationDatas datas) throws
            MigrationException {
        return switch (datas.version.toString()) {
            case "1.0" ->
                migrate_1_0(datas);
            case "2.0" ->
                migrate_2_0(datas);
            default ->
                throw new MigrationException();
        };
    }

    protected MigrationDatas migrate_1_0(MigrationDatas datas) throws
            MigrationException {
        MigrationDatas newDatas = new MigrationDatas(new Version("2.0"));
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
        DateFormat ndf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String prevShowId = "";
        String prevRoomId = "";
        String prevDate = "";
        String prevTotalSeats = "";
        for (Data data : datas.list) {
            Data nd = switch ((Command) data.getCommand()) {
                case SETSHOW -> {
                    if (data.get(0).equals(prevShowId)) {
                        yield null;
                    }
                    prevShowId = data.get(0);
                    yield data;
                }
                case SETROOM -> {
                    if (data.get(0).equals(prevRoomId)) {
                        yield null;
                    }
                    prevRoomId = data.get(0);
                    yield data;
                }
                case SETDATE -> {
                    String date;
                    try {
                        date = ndf.format(df.parse(data.get(0)));
                    } catch (ParseException ex) {
                        System.out.println(data.get(0));
                        throw new MigrationException();
                    }
                    if (date.equals(prevDate)) {
                        yield null;
                    }
                    data.set(0, date);
                    prevDate = date;
                    yield data;
                }
                case SETTOTALSEATS -> {
                    yield null;
                }
                case ADDPRICE, DELETEPRICE -> {
                    data.set(1, switch (data.get(1)) {
                        case "Reservation" ->
                            "BOOKING";
                        default ->
                            "TICKET";
                    });
                    yield data;
                }
                case BOOK, UPDATEBOOKING -> {
                    try {
                        data.set(1, ndf.format(df.parse(data.get(1))));
                    } catch (ParseException ex) {
                        throw new MigrationException();
                    }
                    data.set(3, data.get(3).strip().toUpperCase(Preferences.
                            getLocale()));
                    data.set(4, data.get(4).replace("th??atre", "théâtre"));
                    String data9 = "";
                    if (data.size() > 9) {
                        data9 = data.remove(9);
                    }
                    String data8 = data.remove(8);
                    data.remove(7);
                    data.remove(6);
                    data.add(4, data8.equals("") ? data9 : data8);
                    yield data;
                }
                case UNBOOK -> {
                    try {
                        data.set(1, ndf.format(df.parse(data.get(1))));
                    } catch (ParseException ex) {
                        throw new MigrationException();
                    }
                    data.set(3, data.get(3).strip().toUpperCase(Preferences.
                            getLocale()));
                    data.remove(4);
                    yield data;
                }
                case SELL -> {
                    try {
                        data.set(1, ndf.format(df.parse(data.get(1))));
                    } catch (ParseException ex) {
                        throw new MigrationException();
                    }
                    data.set(2, data.get(2).replace("Adh??rant", "Adhérant"));
                    yield data;
                }
                case UNSELL -> {
                    try {
                        data.set(1, ndf.format(df.parse(data.get(1))));
                    } catch (ParseException ex) {
                        throw new MigrationException();
                    }
                    yield data;
                }
                case UNDEFINED -> {
                    yield switch (data.getCommandName()) {
                        case "SETNBSEATS" -> {
                            String totalSeats = data.get(0);
                            if (totalSeats.equals(prevTotalSeats)) {
                                yield null;
                            }
                            prevTotalSeats = totalSeats;
                            yield new Data(Command.SETTOTALSEATS, totalSeats);
                        }
                        default -> {
                            yield null;
                        }
                    };
                }
                default ->
                    null;
            };
            if (nd != null) {
                newDatas.list.add(nd);
            }
        }
        return newDatas;
    }

    protected MigrationDatas migrate_2_0(MigrationDatas datas) throws
            MigrationException {
        MigrationDatas newDatas = new MigrationDatas(new Version("2.1"));
        Map<String, String> prices = new HashMap<>();
        for (Data data : datas.list) {
            Data nd = switch ((Command) data.getCommand()) {
                case ADDPRICE, UPDATEPRICE -> {
                    prices.put(data.get(2), data.get(4));
                    yield data;
                }
                case BOOK, UPDATEBOOKING -> {
                    data.set(3, data.get(3).strip().toUpperCase(Preferences.
                            getLocale()));
                    String vc = prices.get(data.get(5));
                    if (vc != null) {
                        data.set(5, vc);
                    }
                    yield data;
                }
                case UNBOOK -> {
                    data.set(3, data.get(3).strip().toUpperCase(Preferences.
                            getLocale()));
                    yield data;
                }
                default ->
                    data;
            };
            if (nd != null) {
                newDatas.list.add(nd);
            }
        }
        return newDatas;
    }

    @Override
    protected Function<String, Enum> getValueOf() {
        return (String s) -> {
            try {
                return Command.valueOf(s);
            } catch (IllegalArgumentException ex) {
                return Command.UNDEFINED;
            }
        };
    }
}
