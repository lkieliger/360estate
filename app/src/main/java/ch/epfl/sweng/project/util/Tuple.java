package ch.epfl.sweng.project.util;

import java.util.Objects;

/**
 * This class represent a Tuple of two elements from distinct objects.
 *
 * @param <T>
 * @param <U>
 */
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

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Tuple thatTuple = (Tuple) obj;

        return thatTuple.getX().equals(x) && thatTuple.getY().equals(y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
