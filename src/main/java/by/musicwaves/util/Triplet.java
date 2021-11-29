package by.musicwaves.util;

public class Triplet<S, U, V> extends Pair<S, U> {
    private V thirdValue;

    public Triplet(S firstValue, U secondValue, V thirdValue) {
        super(firstValue, secondValue);
        this.thirdValue = thirdValue;
    }

    public Triplet(V thirdValue) {
        this.thirdValue = thirdValue;
    }

    public V getThirdValue() {
        return thirdValue;
    }

    public void setThirdValue(V thirdValue) {
        this.thirdValue = thirdValue;
    }
}
