package com.sun.corba.se.impl.orbutil;

import com.sun.corba.se.impl.io.TypeMismatchException;
import java.io.Serializable;

public interface RepositoryIdStrings {
   String createForAnyType(Class var1);

   String createForJavaType(Serializable var1) throws TypeMismatchException;

   String createForJavaType(Class var1) throws TypeMismatchException;

   String createSequenceRepID(Object var1);

   String createSequenceRepID(Class var1);

   RepositoryIdInterface getFromString(String var1);

   String getClassDescValueRepId();

   String getWStringValueRepId();
}
