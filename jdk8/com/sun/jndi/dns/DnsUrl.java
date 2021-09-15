package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class DnsUrl extends Uri {
   private String domain;

   public static DnsUrl[] fromList(String var0) throws MalformedURLException {
      DnsUrl[] var1 = new DnsUrl[(var0.length() + 1) / 2];
      int var2 = 0;

      for(StringTokenizer var3 = new StringTokenizer(var0, " "); var3.hasMoreTokens(); var1[var2++] = new DnsUrl(var3.nextToken())) {
      }

      DnsUrl[] var4 = new DnsUrl[var2];
      System.arraycopy(var1, 0, var4, 0, var2);
      return var4;
   }

   public DnsUrl(String var1) throws MalformedURLException {
      super(var1);
      if (!this.scheme.equals("dns")) {
         throw new MalformedURLException(var1 + " is not a valid DNS pseudo-URL");
      } else {
         this.domain = this.path.startsWith("/") ? this.path.substring(1) : this.path;
         this.domain = this.domain.equals("") ? "." : UrlUtil.decode(this.domain);
      }
   }

   public String getDomain() {
      return this.domain;
   }
}
