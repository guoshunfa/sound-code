package com.sun.corba.se.impl.oa;

import com.sun.corba.se.spi.oa.NullServant;
import org.omg.CORBA.SystemException;

public class NullServantImpl implements NullServant {
   private SystemException sysex;

   public NullServantImpl(SystemException var1) {
      this.sysex = var1;
   }

   public SystemException getException() {
      return this.sysex;
   }
}
