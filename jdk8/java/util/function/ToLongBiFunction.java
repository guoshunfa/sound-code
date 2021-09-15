package java.util.function;

@FunctionalInterface
public interface ToLongBiFunction<T, U> {
   long applyAsLong(T var1, U var2);
}
