package com.apple.concurrent;

import java.security.AccessController;
import java.security.PrivilegedAction;

final class LibDispatchNative {
   static native boolean nativeIsDispatchSupported();

   static native long nativeGetMainQueue();

   static native long nativeCreateConcurrentQueue(int var0);

   static native long nativeCreateSerialQueue(String var0);

   static native void nativeReleaseQueue(long var0);

   static native void nativeExecuteAsync(long var0, Runnable var2);

   static native void nativeExecuteSync(long var0, Runnable var2);

   private LibDispatchNative() {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("osx");
            return null;
         }
      });
   }
}
