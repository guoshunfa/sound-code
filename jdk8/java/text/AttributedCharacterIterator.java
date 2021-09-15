package java.text;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface AttributedCharacterIterator extends CharacterIterator {
   int getRunStart();

   int getRunStart(AttributedCharacterIterator.Attribute var1);

   int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> var1);

   int getRunLimit();

   int getRunLimit(AttributedCharacterIterator.Attribute var1);

   int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> var1);

   Map<AttributedCharacterIterator.Attribute, Object> getAttributes();

   Object getAttribute(AttributedCharacterIterator.Attribute var1);

   Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys();

   public static class Attribute implements Serializable {
      private String name;
      private static final Map<String, AttributedCharacterIterator.Attribute> instanceMap = new HashMap(7);
      public static final AttributedCharacterIterator.Attribute LANGUAGE = new AttributedCharacterIterator.Attribute("language");
      public static final AttributedCharacterIterator.Attribute READING = new AttributedCharacterIterator.Attribute("reading");
      public static final AttributedCharacterIterator.Attribute INPUT_METHOD_SEGMENT = new AttributedCharacterIterator.Attribute("input_method_segment");
      private static final long serialVersionUID = -9142742483513960612L;

      protected Attribute(String var1) {
         this.name = var1;
         if (this.getClass() == AttributedCharacterIterator.Attribute.class) {
            instanceMap.put(var1, this);
         }

      }

      public final boolean equals(Object var1) {
         return super.equals(var1);
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public String toString() {
         return this.getClass().getName() + "(" + this.name + ")";
      }

      protected String getName() {
         return this.name;
      }

      protected Object readResolve() throws InvalidObjectException {
         if (this.getClass() != AttributedCharacterIterator.Attribute.class) {
            throw new InvalidObjectException("subclass didn't correctly implement readResolve");
         } else {
            AttributedCharacterIterator.Attribute var1 = (AttributedCharacterIterator.Attribute)instanceMap.get(this.getName());
            if (var1 != null) {
               return var1;
            } else {
               throw new InvalidObjectException("unknown attribute name");
            }
         }
      }
   }
}
