package com.sun.management.jmx;

import javax.management.Notification;

/** @deprecated */
@Deprecated
public class TraceNotification extends Notification {
   public int level;
   public int type;
   public String className;
   public String methodName;
   public String info;
   public Throwable exception;
   public long globalSequenceNumber;
   public long sequenceNumber;

   public TraceNotification(Object var1, long var2, long var4, int var6, int var7, String var8, String var9, String var10, Throwable var11) {
      super((String)null, var1, var2);
      this.sequenceNumber = var2;
      this.globalSequenceNumber = var4;
      this.level = var6;
      this.type = var7;
      this.className = var8 != null ? var8 : "";
      this.methodName = var9 != null ? var9 : "";
      this.info = var10 != null ? var10 : null;
      this.exception = var11;
   }
}
