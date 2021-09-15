package com.sun.jndi.ldap;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.naming.CommunicationException;

final class LdapRequest {
   LdapRequest next;
   int msgId;
   private int gotten;
   private BlockingQueue<BerDecoder> replies;
   private int highWatermark;
   private boolean cancelled;
   private boolean pauseAfterReceipt;
   private boolean completed;

   LdapRequest(int var1, boolean var2) {
      this(var1, var2, -1);
   }

   LdapRequest(int var1, boolean var2, int var3) {
      this.gotten = 0;
      this.highWatermark = -1;
      this.cancelled = false;
      this.pauseAfterReceipt = false;
      this.completed = false;
      this.msgId = var1;
      this.pauseAfterReceipt = var2;
      if (var3 == -1) {
         this.replies = new LinkedBlockingQueue();
      } else {
         this.replies = new LinkedBlockingQueue(var3);
         this.highWatermark = var3 * 80 / 100;
      }

   }

   synchronized void cancel() {
      this.cancelled = true;
      this.notify();
   }

   synchronized boolean addReplyBer(BerDecoder var1) {
      if (this.cancelled) {
         return false;
      } else {
         try {
            this.replies.put(var1);
         } catch (InterruptedException var4) {
         }

         try {
            var1.parseSeq((int[])null);
            var1.parseInt();
            this.completed = var1.peekByte() == 101;
         } catch (IOException var3) {
         }

         var1.reset();
         this.notify();
         return this.highWatermark != -1 && this.replies.size() >= this.highWatermark ? true : this.pauseAfterReceipt;
      }
   }

   synchronized BerDecoder getReplyBer() throws CommunicationException {
      if (this.cancelled) {
         throw new CommunicationException("Request: " + this.msgId + " cancelled");
      } else {
         BerDecoder var1 = (BerDecoder)this.replies.poll();
         return var1;
      }
   }

   synchronized boolean hasSearchCompleted() {
      return this.completed;
   }
}
