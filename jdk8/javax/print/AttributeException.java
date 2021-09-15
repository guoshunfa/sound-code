package javax.print;

import javax.print.attribute.Attribute;

public interface AttributeException {
   Class[] getUnsupportedAttributes();

   Attribute[] getUnsupportedValues();
}
