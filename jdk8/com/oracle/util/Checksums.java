package com.oracle.util;

import java.nio.ByteBuffer;
import java.util.zip.Adler32;

public class Checksums {
   private Checksums() {
   }

   public static void update(Adler32 var0, ByteBuffer var1) {
      var0.update(var1);
   }
}
