package by.musicwaves.util;

public class Pair<S, U> {

    private S firstValue;
    private U secondValue;

    public Pair(S firstValue, U secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public Pair() {
    }

    public S getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(S firstValue) {
        this.firstValue = firstValue;
    }

    public U getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(U secondValue) {
        this.secondValue = secondValue;
    }
}
