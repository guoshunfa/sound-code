package com.sun.corba.se.impl.util;

import java.util.Hashtable;

public class RepositoryIdCache extends Hashtable {
   private RepositoryIdPool pool = new RepositoryIdPool();

   public RepositoryIdCache() {
      this.pool.setCaches(this);
   }

   public final synchronized RepositoryId getId(String var1) {
      RepositoryId var2 = (RepositoryId)super.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new RepositoryId(var1);
         this.put(var1, var2);
         return var2;
      }
   }
}
