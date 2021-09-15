package sun.net.www.protocol.http;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.security.action.GetPropertyAction;

public class AuthenticationHeader {
   MessageHeader rsp;
   HeaderParser preferred;
   String preferred_r;
   private final HttpCallerInfo hci;
   boolean dontUseNegotiate;
   static String authPref = null;
   String hdrname;
   HashMap<String, AuthenticationHeader.SchemeMapValue> schemes;

   public String toString() {
      return "AuthenticationHeader: prefer " + this.preferred_r;
   }

   public AuthenticationHeader(String var1, MessageHeader var2, HttpCallerInfo var3, boolean var4) {
      this(var1, var2, var3, var4, Collections.emptySet());
   }

   public AuthenticationHeader(String var1, MessageHeader var2, HttpCallerInfo var3, boolean var4, Set<String> var5) {
      this.dontUseNegotiate = false;
      this.hci = var3;
      this.dontUseNegotiate = var4;
      this.rsp = var2;
      this.hdrname = var1;
      this.schemes = new HashMap();
      this.parse(var5);
   }

   public HttpCallerInfo getHttpCallerInfo() {
      return this.hci;
   }

   private void parse(Set<String> var1) {
      Iterator var2 = this.rsp.multiValueIterator(this.hdrname);

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         HeaderParser var4 = new HeaderParser(var3);
         Iterator var5 = var4.keys();
         int var6 = 0;

         int var7;
         HeaderParser var8;
         String var9;
         for(var7 = -1; var5.hasNext(); ++var6) {
            var5.next();
            if (var4.findValue(var6) == null) {
               if (var7 != -1) {
                  var8 = var4.subsequence(var7, var6);
                  var9 = var8.findKey(0);
                  if (!var1.contains(var9)) {
                     this.schemes.put(var9, new AuthenticationHeader.SchemeMapValue(var8, var3));
                  }
               }

               var7 = var6;
            }
         }

         if (var6 > var7) {
            var8 = var4.subsequence(var7, var6);
            var9 = var8.findKey(0);
            if (!var1.contains(var9)) {
               this.schemes.put(var9, new AuthenticationHeader.SchemeMapValue(var8, var3));
            }
         }
      }

      AuthenticationHeader.SchemeMapValue var10 = null;
      if (authPref != null && (var10 = (AuthenticationHeader.SchemeMapValue)this.schemes.get(authPref)) != null) {
         if (this.dontUseNegotiate && authPref.equals("negotiate")) {
            var10 = null;
         }
      } else {
         AuthenticationHeader.SchemeMapValue var11;
         if (var10 == null && !this.dontUseNegotiate) {
            var11 = (AuthenticationHeader.SchemeMapValue)this.schemes.get("negotiate");
            if (var11 != null) {
               if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Negotiate"))) {
                  var11 = null;
               }

               var10 = var11;
            }
         }

         if (var10 == null && !this.dontUseNegotiate) {
            var11 = (AuthenticationHeader.SchemeMapValue)this.schemes.get("kerberos");
            if (var11 != null) {
               if (this.hci == null || !NegotiateAuthentication.isSupported(new HttpCallerInfo(this.hci, "Kerberos"))) {
                  var11 = null;
               }

               var10 = var11;
            }
         }

         if (var10 == null && (var10 = (AuthenticationHeader.SchemeMapValue)this.schemes.get("digest")) == null && (!NTLMAuthenticationProxy.supported || (var10 = (AuthenticationHeader.SchemeMapValue)this.schemes.get("ntlm")) == null)) {
            var10 = (AuthenticationHeader.SchemeMapValue)this.schemes.get("basic");
         }
      }

      if (var10 != null) {
         this.preferred = var10.parser;
         this.preferred_r = var10.raw;
      }

   }

   public HeaderParser headerParser() {
      return this.preferred;
   }

   public String scheme() {
      return this.preferred != null ? this.preferred.findKey(0) : null;
   }

   public String raw() {
      return this.preferred_r;
   }

   public boolean isPresent() {
      return this.preferred != null;
   }

   static {
      authPref = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.auth.preference")));
      if (authPref != null) {
         authPref = authPref.toLowerCase();
         if (authPref.equals("spnego") || authPref.equals("kerberos")) {
            authPref = "negotiate";
         }
      }

   }

   static class SchemeMapValue {
      String raw;
      HeaderParser parser;

      SchemeMapValue(HeaderParser var1, String var2) {
         this.raw = var2;
         this.parser = var1;
      }
   }
}
