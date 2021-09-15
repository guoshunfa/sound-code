package com.sun.media.sound;

public class InvalidFormatException extends InvalidDataException {
   private static final long serialVersionUID = 1L;

   public InvalidFormatException() {
      super("Invalid format!");
   }

   public InvalidFormatException(String var1) {
      super(var1);
   }
}
