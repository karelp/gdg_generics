package gdg;

/**
 * Typ, ktery umoznuje nahradit null reference v Jave.
 * Drzi si informaci o tom, zda je hodnota pritomna ci nikoliv.
 * Neumoznuje pracovat s hodnotou, pokud neni pritomna.
 * Jedna se o zjednodusenou variantu Optional<T> z Java 8.
 */
public final class Maybe<T> {
    private T value; // Hodnota
    private boolean present; // Indikator, je-li hodnota pritomna

    private Maybe() {}

    /**
     * Vytvori prazdnou hodnotu (ekvivalent null) daneho typu T.
     * @param <T> typ hodnoty
     * @return prazdna hodnota typu T
     */
    public static <T> Maybe<T> empty() {
        return new Maybe<>();
    }

    /**
     * Vytvori hodnotu daneho typu T.
     * @param value hodnota, ktera se ma do Maybe ulozit
     * @param <T> typ hodnoty
     * @return naplnena hodnota typu T
     */
    public static <T> Maybe<T> ofValue(T value) {
        Maybe<T> res = new Maybe<>();
        res.value = value;
        res.present = true;
        return res;
    }

    /**
     * Umoznuje pristup k hodnote. Spusti kod definovany
     * callbackem pouze pokud je hodnota pritomna. Vraci novou
     * instanci Maybe - pokud byla hodnota pritomna, obsahuje
     * vysledek volani callbacku, jinak obsahuje prazdnou hodnotu.
     * @param callback kod pro praci s existujici hodnotou
     * @param <R> navratovy typ callbacku
     * @return empty(), pokud je aktualni Maybe empty(), jinak vysledek volani zabaleny v Maybe
     */
    public <R> Maybe<R> when(Callback<T, R> callback) {
        if (present) {
            return Maybe.ofValue(callback.call(value));
        }
        return Maybe.<R>empty();
    }

    /**
     * Stejne jako when() vyse, pouze osetruje pripad, kdy callback sam o sobe vraci Maybe.
     * V te chvili when() vyse vraci Maybe<Maybe<R>>, coz je zbytecne - Maybe<Maybe<R>> je
     * ekvivalentni s Maybe<R>.
     * @param callback callback, ktery vraci maybe
     * @param <R> vysledek callbacku nebo empty, pokud je tento Maybe prazdny
     * @return Maybe z callbacku nebo prazdny maybe, pokud je aktualini instance prazdna
     */
    public <R> Maybe<R> when(MaybeCallback<T, R> callback) {
        if (present) {
            return callback.call(value);
        }
        return Maybe.<R>empty();
    }

    /**
     * Metoda, ktera vrati hodnotu, je-li v Maybe pritomna, jinak vrati predanou
     * vychozi hodnotu.
     * @param defaultValue vychozi hodnota
     * @return value pokud present == true, jinak defaultValue
     */
    public T orElse(T defaultValue) {
        if (present)
            return value;
        else
            return defaultValue;
    }

    public interface Callback<ParamType, ResultType> {
        ResultType call(ParamType value);
    }

    public interface MaybeCallback<ParamType, ResultType> {
        Maybe<ResultType> call(ParamType value);
    }
}
