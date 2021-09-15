package com.oracle.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.DirectBuffer;

public final class BufferSecrets<A> {
   private static final JavaNioAccess javaNioAccess = SharedSecrets.getJavaNioAccess();
   private static final BufferSecrets<?> theBufferSecrets = new BufferSecrets();

   private BufferSecrets() {
   }

   public static <A> BufferSecrets<A> instance() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new BufferSecretsPermission("access"));
      }

      return theBufferSecrets;
   }

   public ByteBuffer newDirectByteBuffer(long var1, int var3, A var4) {
      if (var3 < 0) {
         throw new IllegalArgumentException("Negative capacity: " + var3);
      } else {
         return javaNioAccess.newDirectByteBuffer(var1, var3, var4);
      }
   }

   public long address(Buffer var1) {
      if (var1 instanceof DirectBuffer) {
         return ((DirectBuffer)var1).address();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public A attachment(Buffer var1) {
      if (var1 instanceof DirectBuffer) {
         return ((DirectBuffer)var1).attachment();
      } else if (var1 == null) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public void truncate(Buffer var1) {
      javaNioAccess.truncate(var1);
   }
}
