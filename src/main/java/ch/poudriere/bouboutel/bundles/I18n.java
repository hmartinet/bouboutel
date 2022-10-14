/*
 */
package ch.poudriere.bouboutel.bundles;

import ch.poudriere.bouboutel.utils.Preferences;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Currency;

/**
 *
 * @author Hervé Martinet
 */
public class I18n {
    public static String BUNDLE = "ch/poudriere/bouboutel/bundles/I18n";
    private static final DecimalFormat currencyFormat;

    static {
        currencyFormat = new DecimalFormat("0.00");
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        currencyFormat.setDecimalFormatSymbols(sym);

    }

    public static String get(String key, String defaultValue) {
        try {
            return ResourceBundle.getBundle(BUNDLE, Preferences.getLocale()).
                    getString(key);
        } catch (MissingResourceException ex) {
            System.err.println("MISSING translation for key %s".formatted(key));
            return defaultValue == null ? "{missing}" : defaultValue;
        }
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static DecimalFormat getCurrencyFormat() {
        return currencyFormat;
    }

    public static String formatDate(LocalDateTime date) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(
                "EEE dd.MM.yyyy à HH:mm", Preferences.getLocale());
        return date.format(f);
    }
    
    public static String formatDate() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern(
                "EEE dd.MM.yyyy à HH:mm", Preferences.getLocale());
        return (LocalDateTime.now()).format(f);
    }    

    public static String formatCurrency(double amount) {
        return "%s %s".formatted(Preferences.getCurrency().getSymbol(),
                currencyFormat.format(amount));
    }
}
