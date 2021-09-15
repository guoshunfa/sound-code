package java.util.function;

@FunctionalInterface
public interface ToDoubleBiFunction<T, U> {
   double applyAsDouble(T var1, U var2);
}
