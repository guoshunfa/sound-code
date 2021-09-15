package com.sun.corba.se.impl.util;

import org.omg.CORBA.Object;

class StubEntry {
   Object stub;
   boolean mostDerived;

   StubEntry(Object var1, boolean var2) {
      this.stub = var1;
      this.mostDerived = var2;
   }
}
