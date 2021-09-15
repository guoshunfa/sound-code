package com.sun.xml.internal.stream.util;

import java.lang.ref.SoftReference;

public class ThreadLocalBufferAllocator {
   private static ThreadLocal tlba = new ThreadLocal();

   public static BufferAllocator getBufferAllocator() {
      SoftReference bAllocatorRef = (SoftReference)tlba.get();
      if (bAllocatorRef == null || bAllocatorRef.get() == null) {
         bAllocatorRef = new SoftReference(new BufferAllocator());
         tlba.set(bAllocatorRef);
      }

      return (BufferAllocator)bAllocatorRef.get();
   }
}
