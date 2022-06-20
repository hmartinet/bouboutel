/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.utils.Preferences;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author Hervé Martinet
 */
public abstract class AbstractDataFile {
    public interface DataConsumer {
        public void process(Data data) throws IOException;
    }

    public interface DataWriter {
        public void write(DataFileWriter writer) throws IOException;
    }

    public class MigrationDatas {
        public Version version;
        public List<Data> list = new ArrayList<>();

        public MigrationDatas(Version version) {
            this.version = version;
        }
    }
    private final File file;

    public AbstractDataFile(File file) {
        this.file = file;
    }

    private MigrationDatas getMigrationDatas(DataFileReader reader) throws
            IOException {
        MigrationDatas datas = new MigrationDatas(reader.getVersion());
        String s;
        while ((s = reader.readLine()) != null) {
            datas.list.add(Data.fromString(s, getValueOf()));
        }
        return datas;
    }

    private void writeMigrationDatas(MigrationDatas datas) throws IOException {
        write((DataFileWriter writer) -> {
            for (Data data : datas.list) {
                writer.write(data);
            }
        }, datas.version);
    }

    public void read(DataConsumer consumer) throws MigrationException,
            IOException {
        MigrationDatas datas = null;
        try ( DataFileReader reader = new DataFileReader(file, getIdendifier())) {
            if (reader.getVersion().lesserThan(Preferences.VERSION)) {
                datas = getMigrationDatas(reader);
            } else {
                String s;
                while ((s = reader.readLine()) != null) {
                    consumer.process(Data.fromString(s, getValueOf()));
                }
            }
        }
        if (datas != null) {
            writeMigrationDatas(migrate(datas));
            read(consumer);
        }
    }

    public void write(DataWriter dataWriter) throws IOException {
        try ( DataFileWriter writer = new DataFileWriter(file, getIdendifier())) {
            dataWriter.write(writer);
        }
    }

    public void write(DataWriter dataWriter, Version version) throws IOException {
        try ( DataFileWriter writer = new DataFileWriter(file, getIdendifier(),
                version)) {
            dataWriter.write(writer);
        }
    }

    public void append(DataWriter dataWriter) throws IOException {
        try ( DataFileWriter writer = new DataFileWriter(file)) {
            dataWriter.write(writer);
        }
    }

    protected abstract String getIdendifier();

    protected abstract Function<String, Enum> getValueOf();

    protected abstract MigrationDatas migrate(MigrationDatas datas) throws
            MigrationException;
}
