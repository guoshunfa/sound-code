package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class Association {
   private final int associationID;
   private final int maxInStreams;
   private final int maxOutStreams;

   protected Association(int var1, int var2, int var3) {
      this.associationID = var1;
      this.maxInStreams = var2;
      this.maxOutStreams = var3;
   }

   public final int associationID() {
      return this.associationID;
   }

   public final int maxInboundStreams() {
      return this.maxInStreams;
   }

   public final int maxOutboundStreams() {
      return this.maxOutStreams;
   }
}
