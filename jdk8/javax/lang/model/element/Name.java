package javax.lang.model.element;

public interface Name extends CharSequence {
   boolean equals(Object var1);

   int hashCode();

   boolean contentEquals(CharSequence var1);
}
