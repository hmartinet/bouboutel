/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.files.BackupFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Hervé Martinet
 */
public final class Sequences {
    private static final Map<String, Long> currents = new HashMap<>();

    public static Long next(String key) {
        return currents.merge(key, 1L, Long::sum);
    }

    public static void update(String key, Long value) {
        if (value >= currents.getOrDefault(key, 0L)) {
            currents.put(key, value);
        }
    }

    public static void load() throws IOException, BackupAccessException {
        File file = new File("bouboutel.sequences");
        if (!file.exists()) {
            migrate_from_1_0();
            return;
        }
        Properties prop = new Properties();
        try ( InputStream in = new FileInputStream(file)) {
            prop.load(in);
            for (Object key : prop.keySet()) {
                System.out.println("%s = %s".formatted(key, prop.get(key)));
                currents.put((String) key,
                        Long.valueOf((String) prop.get(key)));
            }
        }
    }

    public static void save() throws IOException, BackupAccessException {
        BackupFile file = new BackupFile("bouboutel.sequences");
        if (!file.exists()) {
            file.createNewFile();
        }
        Properties prop = new Properties();
        for (Map.Entry<String, Long> e : currents.entrySet()) {
            prop.put(e.getKey(), e.getValue().toString());
        }
        try ( OutputStream out
                = new FileOutputStream(file)) {
            prop.store(out, null);
        }
        file.backup();
    }

    public static void migrate_from_1_0() throws IOException,
            BackupAccessException {
        File file = new File("bouboutel.statics");
        if (file.exists()) {
            try ( BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file.getPath()), "UTF-8"))) {
                Long perfomanceId = Long.valueOf(reader.readLine());
                Long ticketId = Long.valueOf(reader.readLine());
                Long contactId = Long.valueOf(reader.readLine());
                Long showId = Long.valueOf(reader.readLine());
                Long bookingId = Long.valueOf(reader.readLine());
                Long priceId = Long.valueOf(reader.readLine());
                Long roomId = Long.valueOf(reader.readLine());
                currents.put("booking.id", bookingId);
                currents.put("performance.id", perfomanceId);
                currents.put("company.id", contactId);
                currents.put("price.id", priceId);
                currents.put("room.id", roomId);
                currents.put("show.id", showId);
                currents.put("ticket.id", ticketId);
            }
            save();
            file.delete();
        }
    }
}
