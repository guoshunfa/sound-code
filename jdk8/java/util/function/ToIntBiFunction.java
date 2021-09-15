package java.util.function;

@FunctionalInterface
public interface ToIntBiFunction<T, U> {
   int applyAsInt(T var1, U var2);
}
