package com.sun.demo.jvmti.hprof;

public class Tracker {
   private static int engaged = 0;

   private static native void nativeObjectInit(Object var0, Object var1);

   public static void ObjectInit(Object var0) {
      if (engaged != 0) {
         if (var0 == null) {
            throw new IllegalArgumentException("Null object.");
         }

         nativeObjectInit(Thread.currentThread(), var0);
      }

   }

   private static native void nativeNewArray(Object var0, Object var1);

   public static void NewArray(Object var0) {
      if (engaged != 0) {
         if (var0 == null) {
            throw new IllegalArgumentException("Null object.");
         }

         nativeNewArray(Thread.currentThread(), var0);
      }

   }

   private static native void nativeCallSite(Object var0, int var1, int var2);

   public static void CallSite(int var0, int var1) {
      if (engaged != 0) {
         if (var0 < 0) {
            throw new IllegalArgumentException("Negative class index");
         }

         if (var1 < 0) {
            throw new IllegalArgumentException("Negative method index");
         }

         nativeCallSite(Thread.currentThread(), var0, var1);
      }

   }

   private static native void nativeReturnSite(Object var0, int var1, int var2);

   public static void ReturnSite(int var0, int var1) {
      if (engaged != 0) {
         if (var0 < 0) {
            throw new IllegalArgumentException("Negative class index");
         }

         if (var1 < 0) {
            throw new IllegalArgumentException("Negative method index");
         }

         nativeReturnSite(Thread.currentThread(), var0, var1);
      }

   }
}
