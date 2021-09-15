package com.sun.xml.internal.ws.developer;

import java.lang.reflect.Constructor;
import java.net.CookieHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class HttpConfigFeature extends WebServiceFeature {
   public static final String ID = "http://jax-ws.java.net/features/http-config";
   private static final Constructor cookieManagerConstructor;
   private static final Object cookiePolicy;
   private final CookieHandler cookieJar;

   public HttpConfigFeature() {
      this(getInternalCookieHandler());
   }

   public HttpConfigFeature(CookieHandler cookieJar) {
      this.enabled = true;
      this.cookieJar = cookieJar;
   }

   private static CookieHandler getInternalCookieHandler() {
      try {
         return (CookieHandler)cookieManagerConstructor.newInstance(null, cookiePolicy);
      } catch (Exception var1) {
         throw new WebServiceException(var1);
      }
   }

   public String getID() {
      return "http://jax-ws.java.net/features/http-config";
   }

   public CookieHandler getCookieHandler() {
      return this.cookieJar;
   }

   static {
      Constructor tempConstructor;
      Object tempPolicy;
      Class policyClass;
      try {
         Class policyClass = Class.forName("java.net.CookiePolicy");
         policyClass = Class.forName("java.net.CookieStore");
         tempConstructor = Class.forName("java.net.CookieManager").getConstructor(policyClass, policyClass);
         tempPolicy = policyClass.getField("ACCEPT_ALL").get((Object)null);
      } catch (Exception var6) {
         try {
            policyClass = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookiePolicy");
            Class storeClass = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieStore");
            tempConstructor = Class.forName("com.sun.xml.internal.ws.transport.http.client.CookieManager").getConstructor(storeClass, policyClass);
            tempPolicy = policyClass.getField("ACCEPT_ALL").get((Object)null);
         } catch (Exception var5) {
            throw new WebServiceException(var5);
         }
      }

      cookieManagerConstructor = tempConstructor;
      cookiePolicy = tempPolicy;
   }
}
