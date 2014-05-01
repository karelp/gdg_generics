package gdg.form;

import java.util.Map;

import static gdg.Maybe.Callback;

/**
 * Textove pole, ktere prijima pouze celociselne hodnoty.
 */
public class NumberField extends FormField<Integer> {
    public NumberField(String id) {
        super(id);
    }

    @Override
    public String render() {
        // Maybe nuti osetrit neexistujici hodnotu
        // V Java 8 lze pouzit lambda pro kratsi zapis: getValue().when(value -> value.toString()).orElse("")
        // nebo: getValue().when(Object::toString).orElse("")
        String value = getValue().when(new Callback<Integer, String>() {
            @Override
            public String call(Integer value) {
                return value.toString();
            }
        }).orElse("");
        return "<input type=\"number\" name=\"" + getId() + "\" value=\"" + value + "\" />";
    }

    @Override
    protected Integer parseValueFromHttp(Map<String, String> request) throws InvalidFieldValueException {
        if (request.containsKey(getId())) {
            try {
                return Integer.valueOf(request.get(getId()));
            } catch (NumberFormatException ex) {
                throw new InvalidFieldValueException(this, "Cannot parse number. " + ex.getMessage(), ex);
            }
        }
        throw new InvalidFieldValueException(this, "Field does not exist");
    }
}
