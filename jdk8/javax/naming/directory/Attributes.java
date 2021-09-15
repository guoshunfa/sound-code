package javax.naming.directory;

import java.io.Serializable;
import javax.naming.NamingEnumeration;

public interface Attributes extends Cloneable, Serializable {
   boolean isCaseIgnored();

   int size();

   Attribute get(String var1);

   NamingEnumeration<? extends Attribute> getAll();

   NamingEnumeration<String> getIDs();

   Attribute put(String var1, Object var2);

   Attribute put(Attribute var1);

   Attribute remove(String var1);

   Object clone();
}
