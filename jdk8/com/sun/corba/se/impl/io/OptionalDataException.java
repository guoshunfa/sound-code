package com.sun.corba.se.impl.io;

import java.io.IOException;

public class OptionalDataException extends IOException {
   public int length;
   public boolean eof;

   OptionalDataException(int var1) {
      this.eof = false;
      this.length = var1;
   }

   OptionalDataException(boolean var1) {
      this.length = 0;
      this.eof = var1;
   }
}
