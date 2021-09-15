package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NamingException;

class Header {
   static final int HEADER_SIZE = 12;
   static final short QR_BIT = -32768;
   static final short OPCODE_MASK = 30720;
   static final int OPCODE_SHIFT = 11;
   static final short AA_BIT = 1024;
   static final short TC_BIT = 512;
   static final short RD_BIT = 256;
   static final short RA_BIT = 128;
   static final short RCODE_MASK = 15;
   int xid;
   boolean query;
   int opcode;
   boolean authoritative;
   boolean truncated;
   boolean recursionDesired;
   boolean recursionAvail;
   int rcode;
   int numQuestions;
   int numAnswers;
   int numAuthorities;
   int numAdditionals;

   Header(byte[] var1, int var2) throws NamingException {
      this.decode(var1, var2);
   }

   private void decode(byte[] var1, int var2) throws NamingException {
      try {
         byte var3 = 0;
         if (var2 < 12) {
            throw new CommunicationException("DNS error: corrupted message header");
         } else {
            this.xid = getShort(var1, var3);
            int var6 = var3 + 2;
            short var4 = (short)getShort(var1, var6);
            var6 += 2;
            this.query = (var4 & -32768) == 0;
            this.opcode = (var4 & 30720) >>> 11;
            this.authoritative = (var4 & 1024) != 0;
            this.truncated = (var4 & 512) != 0;
            this.recursionDesired = (var4 & 256) != 0;
            this.recursionAvail = (var4 & 128) != 0;
            this.rcode = var4 & 15;
            this.numQuestions = getShort(var1, var6);
            var6 += 2;
            this.numAnswers = getShort(var1, var6);
            var6 += 2;
            this.numAuthorities = getShort(var1, var6);
            var6 += 2;
            this.numAdditionals = getShort(var1, var6);
            var6 += 2;
         }
      } catch (IndexOutOfBoundsException var5) {
         throw new CommunicationException("DNS error: corrupted message header");
      }
   }

   private static int getShort(byte[] var0, int var1) {
      return (var0[var1] & 255) << 8 | var0[var1 + 1] & 255;
   }
}
