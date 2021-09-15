package javax.print.attribute;

public interface AttributeSet {
   Attribute get(Class<?> var1);

   boolean add(Attribute var1);

   boolean remove(Class<?> var1);

   boolean remove(Attribute var1);

   boolean containsKey(Class<?> var1);

   boolean containsValue(Attribute var1);

   boolean addAll(AttributeSet var1);

   int size();

   Attribute[] toArray();

   void clear();

   boolean isEmpty();

   boolean equals(Object var1);

   int hashCode();
}
