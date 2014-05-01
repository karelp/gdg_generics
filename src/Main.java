import gdg.Condition;
import gdg.Maybe;
import gdg.form.Form;
import gdg.form.InvalidFieldValueException;
import gdg.form.NumberField;
import gdg.form.TextField;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

import static gdg.Maybe.*;

public class Main {

    public static void main(String[] params) {
        // Testuje retezeni podminek - specifictejsi Condition<Double> && obecnejsi Condition<Object>
        Condition<Double> x = new GreaterThan<>(0.0).and(new LessThan<>(5.1)).and(new NotNull());

        // Testuje retezeni podminek - obecnejsi Condition<Object> && specifictejsi Condition<Double>
        Condition<Double> y = new NotNull().and(new LessThan<>(5.0));

        System.out.println(x.evaluate(3.0)); // -> true
        System.out.println(y.evaluate(null)); // -> false

        // Test typove bezpecnosti fomularu
        Form test = new Form();
        TextField name = new TextField("name");
        NumberField age = new NumberField("age");

        // Validacni pravidla
        name.addRule(new RegexMatches("[a-zA-Z]+")); // OK
        // name.addRule(new GreaterThan<Integer>(17)); // CHYBA - validacni pravidlo pro Integer nelze pridat poli typu String!
        age.addRule(new GreaterThan<>(17)).addRule(new LessThan<>(100)); // OK
        age.addRule(new NotNull()); // OK - Integer je Object, hodnotu typu Integer muzeme otestovat na != null
        // age.addRule(new RegexMatches("[0-9]+")); // CHYBA - validacni pravidlo pro String nejde pridat poli typu Integer!

        test.addField(name);
        test.addField(age);


        // Simulace odeslani formulare
        List<InvalidFieldValueException> errors = test.submit(new HashMap<String, String>() {{
            put("name", "John");
            put("age", "9");
        }});

        showHtml("<html><body><pre><font color=\"red\">" + errors + "</font></pre>" + test.render() + "</body></html>");
    }

    /**
     * Podminka, ktera otestuje, ze predana hodnota je mensi nez zadana konstanta (predana pri
     * vytvareni podminky). Podminka pracuje s cisly, ktera jsou zaroven porovnatelna.
     * @param <T> typ porovnatelnych cisel - splnuje napr. Integer, Double ci Float
     */
    static class LessThan<T extends Number & Comparable<T>> extends Condition<T> {
        T lessThanWhat;

        LessThan(T lessThanWhat) {
            this.lessThanWhat = lessThanWhat;
        }

        @Override
        public boolean evaluate(T val) {
            return lessThanWhat.compareTo(val) > 0;
        }
    }

    /**
     * Obdoba LessThan vyse, pouze otocene znamenko porovnani.
     * @param <T> typ porovnatelnych cisel
     */
    static class GreaterThan<T extends Number & Comparable<T>> extends Condition<T> {
        T greaterThanWhat;

        GreaterThan(T greaterThanWhat) {
            this.greaterThanWhat = greaterThanWhat;
        }


        @Override
        public boolean evaluate(T val) {
            return greaterThanWhat.compareTo(val) < 0;
        }
    }

    /**
     * Testuje, ze zadana hodnota neni null. Pracuje nad libovolnym objektem.
     */
    static class NotNull extends Condition<Object> {

        @Override
        public boolean evaluate(Object val) {
            return val != null;
        }
    }

    /**
     * Testuje, zda retezec splnuje dany regularni vyraz.
     */
    static class RegexMatches extends Condition<String> {
        String regex;

        RegexMatches(String regex) {
            this.regex = regex;
        }

        @Override
        public boolean evaluate(String val) {
            return val.matches(regex);
        }
    }

    /**
     * Pomocna metoda pro rychle zobrazeni HTML v okne.
     * @param html HTML pro zobrazeni
     */
    static void showHtml(final String html) {
        final JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setPreferredSize(new Dimension(800, 600));

        Platform.runLater(new Runnable() {
            public void run() {
                WebView browserInstance = new WebView();
                jfxPanel.setScene(new Scene(browserInstance));
                browserInstance.getEngine().loadContent(html);
            }
        });

        JFrame window = new JFrame("Form test");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.add(jfxPanel);
        window.pack();
        window.setVisible(true);
    }
}
