package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public abstract class UserException extends Exception implements IDLEntity {
   protected UserException() {
   }

   protected UserException(String var1) {
      super(var1);
   }
}
