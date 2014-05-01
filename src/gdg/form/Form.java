package gdg.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Obsahuje seznam formularovych poli a jednoduche renderovani
 * do HTML.
 */
public class Form {
    private List<FormField<?>> fields = new ArrayList<>();

    /**
     * Vykresli formular do HTML
     * @return HTML string
     */
    public String render() {
        StringBuilder res = new StringBuilder();
        res.append("<form action=\"\">\n");
        res.append("<table>\n");
        for (FormField<?> field : fields) {
            res.append("  <tr><td>").append(field.getId()).append(":").append("</td><td>");
            res.append(field.render());
            res.append("</td></tr>\n");
        }
        res.append("</table>");
        res.append("</form>");

        return res.toString();
    }

    /**
     * Prida pole do formulare a vrati ho, aby
     * bylo mozne vyuzit "fluent" volani.
     * @param field pole pro pridani
     * @param <T> datovy typ, ktery pole zapouzdruje (String pro TextField, Integer pro NumberField)
     * @param <Field> typ pole (TextField, NumberField, ...)
     * @return field
     */
    public <T, Field extends FormField<T>> Field addField(Field field) {
        fields.add(field);
        return field;
    }

    /**
     * Zpracuje formular na zaklade dat z HTTP pozadavku.
     * @param httpRequest HTTP pozadavek
     * @return seznam chyb ve vyplnenem formulari ci prazdny seznam pri uspechu
     */
    public List<InvalidFieldValueException> submit(Map<String, String> httpRequest) {
        List<InvalidFieldValueException> res = new ArrayList<>();
        for (FormField<?> field : fields) {
            try {
                field.setFromHttpRequest(httpRequest);
            } catch (InvalidFieldValueException ex) {
                res.add(ex);
            }
        }
        return res;
    }
}
