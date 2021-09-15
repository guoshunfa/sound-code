package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Environment;

public class EnvironmentImpl extends Environment {
   private Exception _exc;

   public Exception exception() {
      return this._exc;
   }

   public void exception(Exception var1) {
      this._exc = var1;
   }

   public void clear() {
      this._exc = null;
   }
}
