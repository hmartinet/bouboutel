/*
 */
package ch.poudriere.bouboutel.files;

import ch.poudriere.bouboutel.utils.Preferences;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Function;

/**
 *
 * @author Hervé Martinet
 */
public class Data extends ArrayList<String> {
    private Enum command;
    private String commandName;

    public Data(Enum command, Object... attributes) {
        super();
        this.command = command;
        for (Object o : attributes) {
            add(valueToString(o));
        }
    }

    protected Data() {
        super();
    }

    public Enum getCommand() {
        return command;
    }

    public String getCommandName() {
        return commandName;
    }

    public LocalDateTime getDateTime(int index) {
        return Preferences.dateFromIsoFormat(get(index));
    }

    private String valueToString(Object value) {
        if (value instanceof LocalDateTime date) {
            return Preferences.isoFormat(date);
        }
        return value == null ? "" : value.toString().replace("|", "");
    }

    @Override
    public String toString() {
        return "%s|%s\n".formatted(getCommand(), String.join("|", this));
    }

    public static Data fromString(String s, Function<String, Enum> valueOf) {
        LinkedList<String> args = new LinkedList<>(
                Arrays.asList(s.split("\\|", -1)));
        Data data = new Data();
        data.commandName = args.poll();
        data.command = valueOf.apply(data.commandName);
        data.addAll(args);
        System.out.print("READ %s".formatted(data.toString()));
        return data;
    }
}
