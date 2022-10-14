/*
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.exceptions.MigrationException;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Function;

/**
 *
 * @author Hervé Martinet
 */
public class SetupFile extends AbstractDataFile {
    public static enum Command {
        ADDPERFORMER,
        ADDSHOW,
        ADDROOM,
        ADDPERFORMANCE,
        ADDPRICELIST,
        ADDPRICE
    }

    public SetupFile(File file) {
        super(file);
    }

    @Override
    protected String getIdendifier() {
        return "Setup";
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
        for (Data data : datas.list) {
            newDatas.list.add(switch ((Command) data.getCommand()) {
                case ADDPERFORMER -> {
                    data.remove(7);
                    data.remove(6);
                    data.remove(5);
                    data.remove(4);
                    data.remove(3);
                    data.remove(1);
                    yield data;
                }
                case ADDSHOW -> {
                    data.remove(1);
                    yield data;
                }
                case ADDROOM -> {
                    data.remove(7);
                    data.remove(6);
                    data.remove(5);
                    data.remove(1);
                    yield data;
                }
                case ADDPERFORMANCE -> {
                    data.remove(1);
                    try {
                        data.set(1, ndf.format(df.parse(data.get(1))));
                    } catch (ParseException ex) {
                        throw new MigrationException();
                    }
                    yield data;
                }
                default -> {
                    throw new MigrationException();
                }
            });
        }
        return newDatas;
    }

    protected MigrationDatas migrate_2_0(MigrationDatas datas) throws
            MigrationException {
        datas.version = new Version("2.1");
        return datas;
    }

    @Override
    protected Function<String, Enum> getValueOf() {
        return Command::valueOf;
    }
}
