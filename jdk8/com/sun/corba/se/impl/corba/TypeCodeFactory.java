package com.sun.corba.se.impl.corba;

public interface TypeCodeFactory {
   void setTypeCode(String var1, TypeCodeImpl var2);

   TypeCodeImpl getTypeCode(String var1);

   void setTypeCodeForClass(Class var1, TypeCodeImpl var2);

   TypeCodeImpl getTypeCodeForClass(Class var1);
}
