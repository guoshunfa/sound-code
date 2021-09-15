package com.sun.jndi.dns;

import java.lang.ref.SoftReference;
import java.util.Date;

class ZoneNode extends NameNode {
   private SoftReference<NameNode> contentsRef = null;
   private long serialNumber = -1L;
   private Date expiration = null;

   ZoneNode(String var1) {
      super(var1);
   }

   protected NameNode newNameNode(String var1) {
      return new ZoneNode(var1);
   }

   synchronized void depopulate() {
      this.contentsRef = null;
      this.serialNumber = -1L;
   }

   synchronized boolean isPopulated() {
      return this.getContents() != null;
   }

   synchronized NameNode getContents() {
      return this.contentsRef != null ? (NameNode)this.contentsRef.get() : null;
   }

   synchronized boolean isExpired() {
      return this.expiration != null && this.expiration.before(new Date());
   }

   ZoneNode getDeepestPopulated(DnsName var1) {
      ZoneNode var2 = this;
      ZoneNode var3 = this.isPopulated() ? this : null;

      for(int var4 = 1; var4 < var1.size(); ++var4) {
         var2 = (ZoneNode)var2.get(var1.getKey(var4));
         if (var2 == null) {
            break;
         }

         if (var2.isPopulated()) {
            var3 = var2;
         }
      }

      return var3;
   }

   NameNode populate(DnsName var1, ResourceRecords var2) {
      NameNode var3 = new NameNode((String)null);

      for(int var4 = 0; var4 < var2.answer.size(); ++var4) {
         ResourceRecord var5 = (ResourceRecord)var2.answer.elementAt(var4);
         DnsName var6 = var5.getName();
         if (var6.size() > var1.size() && var6.startsWith(var1)) {
            NameNode var7 = var3.add(var6, var1.size());
            if (var5.getType() == 2) {
               var7.setZoneCut(true);
            }
         }
      }

      ResourceRecord var10 = (ResourceRecord)var2.answer.firstElement();
      synchronized(this) {
         this.contentsRef = new SoftReference(var3);
         this.serialNumber = getSerialNumber(var10);
         this.setExpiration(getMinimumTtl(var10));
         return var3;
      }
   }

   private void setExpiration(long var1) {
      this.expiration = new Date(System.currentTimeMillis() + 1000L * var1);
   }

   private static long getMinimumTtl(ResourceRecord var0) {
      String var1 = (String)var0.getRdata();
      int var2 = var1.lastIndexOf(32) + 1;
      return Long.parseLong(var1.substring(var2));
   }

   int compareSerialNumberTo(ResourceRecord var1) {
      return ResourceRecord.compareSerialNumbers(this.serialNumber, getSerialNumber(var1));
   }

   private static long getSerialNumber(ResourceRecord var0) {
      String var1 = (String)var0.getRdata();
      int var2 = var1.length();
      int var3 = -1;

      for(int var4 = 0; var4 < 5; ++var4) {
         var3 = var2;
         var2 = var1.lastIndexOf(32, var2 - 1);
      }

      return Long.parseLong(var1.substring(var2 + 1, var3));
   }
}
