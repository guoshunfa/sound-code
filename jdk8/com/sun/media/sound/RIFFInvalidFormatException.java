package com.sun.media.sound;

public final class RIFFInvalidFormatException extends InvalidFormatException {
   private static final long serialVersionUID = 1L;

   public RIFFInvalidFormatException() {
      super("Invalid format!");
   }

   public RIFFInvalidFormatException(String var1) {
      super(var1);
   }
}
