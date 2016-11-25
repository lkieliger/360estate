package ch.epfl.sweng.project.util;

public final class Tuple<T, U> {

    private final T x;
    private final U y;

    public Tuple(T x, U y) {
        this.x = x;
        this.y = y;
    }

    public T getX() {
        return x;
    }

    public U getY() {
        return y;
    }
}
