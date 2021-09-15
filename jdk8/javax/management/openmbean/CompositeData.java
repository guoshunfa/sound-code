package javax.management.openmbean;

import java.util.Collection;

public interface CompositeData {
   CompositeType getCompositeType();

   Object get(String var1);

   Object[] getAll(String[] var1);

   boolean containsKey(String var1);

   boolean containsValue(Object var1);

   Collection<?> values();

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
