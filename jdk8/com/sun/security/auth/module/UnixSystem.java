package com.sun.security.auth.module;

import jdk.Exported;

@Exported
public class UnixSystem {
   protected String username;
   protected long uid;
   protected long gid;
   protected long[] groups;

   private native void getUnixInfo();

   public UnixSystem() {
      System.loadLibrary("jaas_unix");
      this.getUnixInfo();
   }

   public String getUsername() {
      return this.username;
   }

   public long getUid() {
      return this.uid;
   }

   public long getGid() {
      return this.gid;
   }

   public long[] getGroups() {
      return this.groups == null ? null : (long[])this.groups.clone();
   }
}
