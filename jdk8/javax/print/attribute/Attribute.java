package javax.print.attribute;

import java.io.Serializable;

public interface Attribute extends Serializable {
   Class<? extends Attribute> getCategory();

   String getName();
}
