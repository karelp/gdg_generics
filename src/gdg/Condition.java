package gdg;

/**
 * Obecna trida podminky, ktera omezuje hodnoty pro nejaky typ Type.
 */
public abstract class Condition<Type> {
    /**
     * Vypocita, jestli je podminka splnena pro danou hodnotu.
     * @param val hodnota, pro kterou se podminka overi
     * @return true if the condition is met
     */
    public abstract boolean evaluate(Type val);

    /**
     * AND operator pro dve podminky (this && oth). Umoznuje spojit tuto
     * podminku s obecnejsi podminkou - napr. Condition<Integer> and Condition<Object>.
     * Vyslednym typem musi byt vzdy ten specifictejsi, v tomto pripade T.
     * @param oth druha podminka pro and
     * @return konjunkci obou podminek
     */
    public Condition<Type> and(final Condition<? super Type> oth) {
        return new Condition<Type>() {
            @Override
            public boolean evaluate(Type val) {
                return Condition.this.evaluate(val) && oth.evaluate(val);
            }
        };
    }

    /**
     * Pretizena metoda pro AND dvou podminek. Oproti and() vyse umoznuje spojit
     * obecnou podminku se specifictejsi - napr. Condition<Object> and Condition<Integer>.
     * Vyslednym typem musi byt vzdy ten specifictejsi, v tomto pripade OthType.
     * Kvuli Java type erasure je nutne pridat variadicky argument typu Void..., aby Java
     * byla schopna rozlisit tento and() a and() vyse.
     * @param oth druha podminka pro and
     * @param unused nepouzity variadicky parametr - pouze workaround pro type erasure
     * @param <OthType> typ druhe podminky, omezeny tak, aby byl specifictejsi nez Type
     * @return konjunkci obou podminek
     */
    public <OthType extends Type> Condition<OthType> and(final Condition<OthType> oth, Void... unused) {
        return new Condition<OthType>() {
            @Override
            public boolean evaluate(OthType val) {
                return Condition.this.evaluate(val) && oth.evaluate(val);
            }
        };
    }

    public Condition<Type> or(final Condition<? super Type> oth) {
        return new Condition<Type>() {
            @Override
            public boolean evaluate(Type val) {
                return Condition.this.evaluate(val) || oth.evaluate(val);
            }
        };
    }

    public <OthType extends Type> Condition<OthType> or(final Condition<OthType> oth, Void... unused) {
        return new Condition<OthType>() {
            @Override
            public boolean evaluate(OthType val) {
                return Condition.this.evaluate(val) || oth.evaluate(val);
            }
        };
    }

    public Condition<Type> not() {
        return new Condition<Type>() {
            @Override
            public boolean evaluate(Type val) {
                return !Condition.this.evaluate(val);
            }
        };
    }
}
