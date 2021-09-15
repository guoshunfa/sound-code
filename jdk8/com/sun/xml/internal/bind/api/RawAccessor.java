package com.sun.xml.internal.bind.api;

public abstract class RawAccessor<B, V> {
   public abstract V get(B var1) throws AccessorException;

   public abstract void set(B var1, V var2) throws AccessorException;
}
