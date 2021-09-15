package javax.swing.text;

import java.util.Enumeration;

public interface AttributeSet {
   Object NameAttribute = StyleConstants.NameAttribute;
   Object ResolveAttribute = StyleConstants.ResolveAttribute;

   int getAttributeCount();

   boolean isDefined(Object var1);

   boolean isEqual(AttributeSet var1);

   AttributeSet copyAttributes();

   Object getAttribute(Object var1);

   Enumeration<?> getAttributeNames();

   boolean containsAttribute(Object var1, Object var2);

   boolean containsAttributes(AttributeSet var1);

   AttributeSet getResolveParent();

   public interface ParagraphAttribute {
   }

   public interface CharacterAttribute {
   }

   public interface ColorAttribute {
   }

   public interface FontAttribute {
   }
}
