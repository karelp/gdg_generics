package gdg.form;

import java.util.Map;

import static gdg.Maybe.Callback;

/**
 * Textove pole, ktere prijima retezcove hodnoty.
 */
public class TextField extends FormField<String> {

    public TextField(String id) {
        super(id);
    }

    @Override
    public String render() {
        // Maybe nuti osetreni chybejici hodnoty
        // V Java 8 je mozne pouzit lambda vyraz pro prehlednost: String value = getValue().when(value -> value.replace("\"", "\\\"")).orElse("");
        String value = getValue().when(new Callback<String, String>() {
            @Override
            public String call(String value) {
                return value.replace("\"", "\\\""); // trivialni XSS filtr :)
            }
        }).orElse("");

        return "<input type=\"text\" name=\"" + getId() + "\" value=\"" + value + "\" />";
    }

    @Override
    protected String parseValueFromHttp(Map<String, String> request) throws InvalidFieldValueException {
        if (request.containsKey(getId())) {
            return request.get(getId());
        }
        throw new InvalidFieldValueException(this, "Field is missing");
    }
}
