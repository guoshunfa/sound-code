package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;

public interface Name extends Cloneable, Serializable, Comparable<Object> {
   long serialVersionUID = -3617482732056931635L;

   Object clone();

   int compareTo(Object var1);

   int size();

   boolean isEmpty();

   Enumeration<String> getAll();

   String get(int var1);

   Name getPrefix(int var1);

   Name getSuffix(int var1);

   boolean startsWith(Name var1);

   boolean endsWith(Name var1);

   Name addAll(Name var1) throws InvalidNameException;

   Name addAll(int var1, Name var2) throws InvalidNameException;

   Name add(String var1) throws InvalidNameException;

   Name add(int var1, String var2) throws InvalidNameException;

   Object remove(int var1) throws InvalidNameException;
}
