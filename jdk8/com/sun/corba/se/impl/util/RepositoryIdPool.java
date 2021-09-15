package com.sun.corba.se.impl.util;

import java.util.EmptyStackException;
import java.util.Stack;

class RepositoryIdPool extends Stack {
   private static int MAX_CACHE_SIZE = 4;
   private RepositoryIdCache cache;

   public final synchronized RepositoryId popId() {
      try {
         return (RepositoryId)super.pop();
      } catch (EmptyStackException var2) {
         this.increasePool(5);
         return (RepositoryId)super.pop();
      }
   }

   final void increasePool(int var1) {
      for(int var2 = var1; var2 > 0; --var2) {
         this.push(new RepositoryId());
      }

   }

   final void setCaches(RepositoryIdCache var1) {
      this.cache = var1;
   }
}
