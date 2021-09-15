package com.sun.xml.internal.ws.spi.db;

public interface PropertyAccessor<B, V> {
   V get(B var1) throws DatabindingException;

   void set(B var1, V var2) throws DatabindingException;
}
