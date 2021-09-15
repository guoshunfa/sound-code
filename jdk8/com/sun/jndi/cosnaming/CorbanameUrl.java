package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import javax.naming.Name;
import javax.naming.NamingException;

public final class CorbanameUrl {
   private String stringName;
   private String location;

   public String getStringName() {
      return this.stringName;
   }

   public Name getCosName() throws NamingException {
      return CNCtx.parser.parse(this.stringName);
   }

   public String getLocation() {
      return "corbaloc:" + this.location;
   }

   public CorbanameUrl(String var1) throws MalformedURLException {
      if (!var1.startsWith("corbaname:")) {
         throw new MalformedURLException("Invalid corbaname URL: " + var1);
      } else {
         byte var2 = 10;
         int var3 = var1.indexOf(35, var2);
         if (var3 < 0) {
            var3 = var1.length();
            this.stringName = "";
         } else {
            this.stringName = UrlUtil.decode(var1.substring(var3 + 1));
         }

         this.location = var1.substring(var2, var3);
         int var4 = this.location.indexOf("/");
         if (var4 >= 0) {
            if (var4 == this.location.length() - 1) {
               this.location = this.location + "NameService";
            }
         } else {
            this.location = this.location + "/NameService";
         }

      }
   }
}
