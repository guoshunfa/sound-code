package com.sun.media.sound;

import java.io.IOException;

public class InvalidDataException extends IOException {
   private static final long serialVersionUID = 1L;

   public InvalidDataException() {
      super("Invalid Data!");
   }

   public InvalidDataException(String var1) {
      super(var1);
   }
}
