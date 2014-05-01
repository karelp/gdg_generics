package gdg.form;

import gdg.Condition;
import gdg.Maybe;

import java.util.Map;

/**
 * Predek vsech formularovych poli. Ma svuj typ T, ktery
 * omezuje, jake hodnoty je do nej mozne ukladat.
 */
public abstract class FormField<T> {
    private String id;

    // Validacni podminky - retezi se pres and(), proto staci pouze Condition<T> a nikoliv List<Condition<T>>
    // Vychozi podminka prijima vsechny hodnoty -> vraci vzdy true
    private Condition<T> validationCondition = new Condition<T>() {
        @Override
        public boolean evaluate(T val) {
            return true;
        }
    };

    // Hodnota policka - muze a nemusi byt pritomna. Pouziva typ Maybe, aby bylo zrejme, ze hodnota nemusi existovat
    // bez nutnosti to psat do dokumentace. Zaroven donuti vsechen kod osetrit pripady, kdy hodnota neexistuje.
    private Maybe<T> value = Maybe.<T>empty();

    public FormField(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // Java spravne donuti propagovat Maybe i do getteru - kdokoliv getter vola musi osetrit chybejici hodnotu
    public Maybe<T> getValue() {
        return value;
    }

    /**
     * Vrati HTML podobu formularoveho pole.
     * @return HTML
     */
    public abstract String render();

    /**
     * Prida validacni pravidlo. Vraci this, aby bylo mozne
     * pravidla retezit: field.addRule(podminka1).addRule(podminka2) ...
     * @param condition validacni podminka
     * @return this
     */
    public FormField<T> addRule(Condition<? super T> condition)  {
        validationCondition = validationCondition.and(condition);
        return this;
    }

    /**
     * Precte hodnotu pole z HTTP pozadavku
     * @param request HTTP pozadavek
     * @throws InvalidFieldValueException pokud se nepodari prevest HTTP pozadavek na typ T nebo neprojdou validacni podminky
     */
    public void setFromHttpRequest(Map<String, String> request) throws InvalidFieldValueException {
        T tmpValue = parseValueFromHttp(request);

        if (!validationCondition.evaluate(tmpValue)) {
            throw new InvalidFieldValueException(this, "Field validation failed");
        }

        value = Maybe.ofValue(tmpValue); // Hodnota je od ted pritomna
    }

    /**
     * Prevede HTTP pozadavek na hodnotu typu T.
     * @param request HTTP pozadavek
     * @return nova hodnota pole
     * @throws InvalidFieldValueException pokud se hodnotu nepodarilo prevest
     */
    protected abstract T parseValueFromHttp(Map<String, String> request) throws InvalidFieldValueException;
}
