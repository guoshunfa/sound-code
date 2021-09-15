package javax.management;

import java.io.Serializable;

public interface Descriptor extends Serializable, Cloneable {
   Object getFieldValue(String var1) throws RuntimeOperationsException;

   void setField(String var1, Object var2) throws RuntimeOperationsException;

   String[] getFields();

   String[] getFieldNames();

   Object[] getFieldValues(String... var1);

   void removeField(String var1);

   void setFields(String[] var1, Object[] var2) throws RuntimeOperationsException;

   Object clone() throws RuntimeOperationsException;

   boolean isValid() throws RuntimeOperationsException;

   boolean equals(Object var1);

   int hashCode();
}
