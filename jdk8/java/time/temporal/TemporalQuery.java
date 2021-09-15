package java.time.temporal;

@FunctionalInterface
public interface TemporalQuery<R> {
   R queryFrom(TemporalAccessor var1);
}
