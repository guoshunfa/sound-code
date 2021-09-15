package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

class Resolver {
   private DnsClient dnsClient;
   private int timeout;
   private int retries;

   Resolver(String[] var1, int var2, int var3) throws NamingException {
      this.timeout = var2;
      this.retries = var3;
      this.dnsClient = new DnsClient(var1, var2, var3);
   }

   public void close() {
      this.dnsClient.close();
      this.dnsClient = null;
   }

   ResourceRecords query(DnsName var1, int var2, int var3, boolean var4, boolean var5) throws NamingException {
      return this.dnsClient.query(var1, var2, var3, var4, var5);
   }

   ResourceRecords queryZone(DnsName var1, int var2, boolean var3) throws NamingException {
      DnsClient var4 = new DnsClient(this.findNameServers(var1, var3), this.timeout, this.retries);

      ResourceRecords var5;
      try {
         var5 = var4.queryZone(var1, var2, var3);
      } finally {
         var4.close();
      }

      return var5;
   }

   DnsName findZoneName(DnsName var1, int var2, boolean var3) throws NamingException {
      for(var1 = (DnsName)var1.clone(); var1.size() > 1; var1.remove(var1.size() - 1)) {
         ResourceRecords var4 = null;

         try {
            var4 = this.query(var1, var2, 6, var3, false);
         } catch (NameNotFoundException var8) {
            throw var8;
         } catch (NamingException var9) {
         }

         if (var4 != null) {
            if (var4.answer.size() > 0) {
               return var1;
            }

            for(int var5 = 0; var5 < var4.authority.size(); ++var5) {
               ResourceRecord var6 = (ResourceRecord)var4.authority.elementAt(var5);
               if (var6.getType() == 6) {
                  DnsName var7 = var6.getName();
                  if (var1.endsWith(var7)) {
                     return var7;
                  }
               }
            }
         }
      }

      return var1;
   }

   ResourceRecord findSoa(DnsName var1, int var2, boolean var3) throws NamingException {
      ResourceRecords var4 = this.query(var1, var2, 6, var3, false);

      for(int var5 = 0; var5 < var4.answer.size(); ++var5) {
         ResourceRecord var6 = (ResourceRecord)var4.answer.elementAt(var5);
         if (var6.getType() == 6) {
            return var6;
         }
      }

      return null;
   }

   private String[] findNameServers(DnsName var1, boolean var2) throws NamingException {
      ResourceRecords var3 = this.query(var1, 1, 2, var2, false);
      String[] var4 = new String[var3.answer.size()];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         ResourceRecord var6 = (ResourceRecord)var3.answer.elementAt(var5);
         if (var6.getType() != 2) {
            throw new CommunicationException("Corrupted DNS message");
         }

         var4[var5] = (String)var6.getRdata();
         var4[var5] = var4[var5].substring(0, var4[var5].length() - 1);
      }

      return var4;
   }
}
