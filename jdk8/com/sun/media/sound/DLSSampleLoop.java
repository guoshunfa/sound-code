package com.sun.media.sound;

public final class DLSSampleLoop {
   public static final int LOOP_TYPE_FORWARD = 0;
   public static final int LOOP_TYPE_RELEASE = 1;
   long type;
   long start;
   long length;

   public long getLength() {
      return this.length;
   }

   public void setLength(long var1) {
      this.length = var1;
   }

   public long getStart() {
      return this.start;
   }

   public void setStart(long var1) {
      this.start = var1;
   }

   public long getType() {
      return this.type;
   }

   public void setType(long var1) {
      this.type = var1;
   }
}
