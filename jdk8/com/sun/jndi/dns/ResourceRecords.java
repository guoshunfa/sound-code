package com.sun.jndi.dns;

import java.util.Vector;
import javax.naming.CommunicationException;
import javax.naming.NamingException;

class ResourceRecords {
   Vector<ResourceRecord> question = new Vector();
   Vector<ResourceRecord> answer = new Vector();
   Vector<ResourceRecord> authority = new Vector();
   Vector<ResourceRecord> additional = new Vector();
   boolean zoneXfer;

   ResourceRecords(byte[] var1, int var2, Header var3, boolean var4) throws NamingException {
      if (var4) {
         this.answer.ensureCapacity(8192);
      }

      this.zoneXfer = var4;
      this.add(var1, var2, var3);
   }

   int getFirstAnsType() {
      return this.answer.size() == 0 ? -1 : ((ResourceRecord)this.answer.firstElement()).getType();
   }

   int getLastAnsType() {
      return this.answer.size() == 0 ? -1 : ((ResourceRecord)this.answer.lastElement()).getType();
   }

   void add(byte[] var1, int var2, Header var3) throws NamingException {
      int var5 = 12;

      try {
         ResourceRecord var4;
         int var6;
         for(var6 = 0; var6 < var3.numQuestions; ++var6) {
            var4 = new ResourceRecord(var1, var2, var5, true, false);
            if (!this.zoneXfer) {
               this.question.addElement(var4);
            }

            var5 += var4.size();
         }

         for(var6 = 0; var6 < var3.numAnswers; ++var6) {
            var4 = new ResourceRecord(var1, var2, var5, false, !this.zoneXfer);
            this.answer.addElement(var4);
            var5 += var4.size();
         }

         if (!this.zoneXfer) {
            for(var6 = 0; var6 < var3.numAuthorities; ++var6) {
               var4 = new ResourceRecord(var1, var2, var5, false, true);
               this.authority.addElement(var4);
               var5 += var4.size();
            }

         }
      } catch (IndexOutOfBoundsException var7) {
         throw new CommunicationException("DNS error: corrupted message");
      }
   }
}
