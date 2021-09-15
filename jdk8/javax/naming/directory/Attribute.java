package javax.naming.directory;

import java.io.Serializable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public interface Attribute extends Cloneable, Serializable {
   long serialVersionUID = 8707690322213556804L;

   NamingEnumeration<?> getAll() throws NamingException;

   Object get() throws NamingException;

   int size();

   String getID();

   boolean contains(Object var1);

   boolean add(Object var1);

   boolean remove(Object var1);

   void clear();

   DirContext getAttributeSyntaxDefinition() throws NamingException;

   DirContext getAttributeDefinition() throws NamingException;

   Object clone();

   boolean isOrdered();

   Object get(int var1) throws NamingException;

   Object remove(int var1);

   void add(int var1, Object var2);

   Object set(int var1, Object var2);
}
