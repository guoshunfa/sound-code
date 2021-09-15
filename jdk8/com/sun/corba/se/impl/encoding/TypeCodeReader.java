package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA_2_3.portable.InputStream;

public interface TypeCodeReader extends MarshalInputStream {
   void addTypeCodeAtPosition(TypeCodeImpl var1, int var2);

   TypeCodeImpl getTypeCodeAtPosition(int var1);

   void setEnclosingInputStream(InputStream var1);

   TypeCodeReader getTopLevelStream();

   int getTopLevelPosition();

   int getPosition();

   void printTypeMap();
}
