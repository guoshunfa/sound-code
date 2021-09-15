package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
import com.sun.corba.se.impl.naming.namingutil.CorbanameURL;
import com.sun.corba.se.impl.naming.namingutil.IIOPEndpointInfo;
import com.sun.corba.se.impl.naming.namingutil.INSURL;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import sun.corba.EncapsInputStreamFactory;

public class INSURLOperationImpl implements Operation {
   ORB orb;
   ORBUtilSystemException wrapper;
   OMGSystemException omgWrapper;
   Resolver bootstrapResolver;
   private NamingContextExt rootNamingContextExt;
   private Object rootContextCacheLock = new Object();
   private INSURLHandler insURLHandler = INSURLHandler.getINSURLHandler();
   private static final int NIBBLES_PER_BYTE = 2;
   private static final int UN_SHIFT = 4;

   public INSURLOperationImpl(ORB var1, Resolver var2) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "orb.resolver");
      this.omgWrapper = OMGSystemException.get(var1, "orb.resolver");
      this.bootstrapResolver = var2;
   }

   private org.omg.CORBA.Object getIORFromString(String var1) {
      if ((var1.length() & 1) == 1) {
         throw this.wrapper.badStringifiedIorLen();
      } else {
         byte[] var2 = new byte[(var1.length() - "IOR:".length()) / 2];
         int var3 = "IOR:".length();

         for(int var4 = 0; var3 < var1.length(); ++var4) {
            var2[var4] = (byte)(ORBUtility.hexOf(var1.charAt(var3)) << 4 & 240);
            var2[var4] |= (byte)(ORBUtility.hexOf(var1.charAt(var3 + 1)) & 15);
            var3 += 2;
         }

         EncapsInputStream var5 = EncapsInputStreamFactory.newEncapsInputStream(this.orb, var2, var2.length, this.orb.getORBData().getGIOPVersion());
         var5.consumeEndian();
         return var5.read_Object();
      }
   }

   public Object operate(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         if (var2.startsWith("IOR:")) {
            return this.getIORFromString(var2);
         } else {
            INSURL var3 = this.insURLHandler.parseURL(var2);
            if (var3 == null) {
               throw this.omgWrapper.soBadSchemeName();
            } else {
               return this.resolveINSURL(var3);
            }
         }
      } else {
         throw this.wrapper.stringExpected();
      }
   }

   private org.omg.CORBA.Object resolveINSURL(INSURL var1) {
      return var1.isCorbanameURL() ? this.resolveCorbaname((CorbanameURL)var1) : this.resolveCorbaloc((CorbalocURL)var1);
   }

   private org.omg.CORBA.Object resolveCorbaloc(CorbalocURL var1) {
      org.omg.CORBA.Object var2 = null;
      if (var1.getRIRFlag()) {
         var2 = this.bootstrapResolver.resolve(var1.getKeyString());
      } else {
         var2 = this.getIORUsingCorbaloc(var1);
      }

      return var2;
   }

   private org.omg.CORBA.Object resolveCorbaname(CorbanameURL var1) {
      Object var2 = null;

      try {
         NamingContextExt var3 = null;
         if (var1.getRIRFlag()) {
            var3 = this.getDefaultRootNamingContext();
         } else {
            org.omg.CORBA.Object var4 = this.getIORUsingCorbaloc(var1);
            if (var4 == null) {
               return null;
            }

            var3 = NamingContextExtHelper.narrow(var4);
         }

         String var6 = var1.getStringifiedName();
         return (org.omg.CORBA.Object)(var6 == null ? var3 : var3.resolve_str(var6));
      } catch (Exception var5) {
         this.clearRootNamingContextCache();
         return null;
      }
   }

   private org.omg.CORBA.Object getIORUsingCorbaloc(INSURL var1) {
      HashMap var2 = new HashMap();
      ArrayList var3 = new ArrayList();
      List var4 = var1.getEndpointInfo();
      String var5 = var1.getKeyString();
      if (var5 == null) {
         return null;
      } else {
         ObjectKey var6 = this.orb.getObjectKeyFactory().create(var5.getBytes());
         IORTemplate var7 = IORFactories.makeIORTemplate(var6.getTemplate());
         Iterator var8 = var4.iterator();

         while(var8.hasNext()) {
            IIOPEndpointInfo var9 = (IIOPEndpointInfo)var8.next();
            IIOPAddress var10 = IIOPFactories.makeIIOPAddress(this.orb, var9.getHost(), var9.getPort());
            GIOPVersion var11 = GIOPVersion.getInstance((byte)var9.getMajor(), (byte)var9.getMinor());
            IIOPProfileTemplate var12 = null;
            if (var11.equals(GIOPVersion.V1_0)) {
               var12 = IIOPFactories.makeIIOPProfileTemplate(this.orb, var11, var10);
               var3.add(var12);
            } else if (var2.get(var11) == null) {
               var12 = IIOPFactories.makeIIOPProfileTemplate(this.orb, var11, var10);
               var2.put(var11, var12);
            } else {
               var12 = (IIOPProfileTemplate)var2.get(var11);
               AlternateIIOPAddressComponent var13 = IIOPFactories.makeAlternateIIOPAddressComponent(var10);
               var12.add(var13);
            }
         }

         GIOPVersion var15 = this.orb.getORBData().getGIOPVersion();
         IIOPProfileTemplate var16 = (IIOPProfileTemplate)var2.get(var15);
         if (var16 != null) {
            var7.add(var16);
            var2.remove(var15);
         }

         Comparator var17 = new Comparator() {
            public int compare(Object var1, Object var2) {
               GIOPVersion var3 = (GIOPVersion)var1;
               GIOPVersion var4 = (GIOPVersion)var2;
               return var3.lessThan(var4) ? 1 : (var3.equals(var4) ? 0 : -1);
            }
         };
         ArrayList var18 = new ArrayList(var2.keySet());
         Collections.sort(var18, var17);
         Iterator var19 = var18.iterator();

         while(var19.hasNext()) {
            IIOPProfileTemplate var14 = (IIOPProfileTemplate)var2.get(var19.next());
            var7.add(var14);
         }

         var7.addAll(var3);
         IOR var20 = var7.makeIOR(this.orb, "", var6.getId());
         return ORBUtility.makeObjectReference(var20);
      }
   }

   private NamingContextExt getDefaultRootNamingContext() {
      synchronized(this.rootContextCacheLock) {
         if (this.rootNamingContextExt == null) {
            try {
               this.rootNamingContextExt = NamingContextExtHelper.narrow(this.orb.getLocalResolver().resolve("NameService"));
            } catch (Exception var4) {
               this.rootNamingContextExt = null;
            }
         }
      }

      return this.rootNamingContextExt;
   }

   private void clearRootNamingContextCache() {
      synchronized(this.rootContextCacheLock) {
         this.rootNamingContextExt = null;
      }
   }
}
