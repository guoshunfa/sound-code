package javax.management.openmbean;

import java.util.Collection;
import java.util.Set;

public interface TabularData {
   TabularType getTabularType();

   Object[] calculateIndex(CompositeData var1);

   int size();

   boolean isEmpty();

   boolean containsKey(Object[] var1);

   boolean containsValue(CompositeData var1);

   CompositeData get(Object[] var1);

   void put(CompositeData var1);

   CompositeData remove(Object[] var1);

   void putAll(CompositeData[] var1);

   void clear();

   Set<?> keySet();

   Collection<?> values();

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
