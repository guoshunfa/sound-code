package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.HashSet;
import java.util.Set;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class BootstrapResolverImpl implements Resolver {
   private Delegate bootstrapDelegate;
   private ORBUtilSystemException wrapper;

   public BootstrapResolverImpl(ORB var1, String var2, int var3) {
      this.wrapper = ORBUtilSystemException.get(var1, "orb.resolver");
      byte[] var4 = "INIT".getBytes();
      ObjectKey var5 = var1.getObjectKeyFactory().create(var4);
      IIOPAddress var6 = IIOPFactories.makeIIOPAddress(var1, var2, var3);
      IIOPProfileTemplate var7 = IIOPFactories.makeIIOPProfileTemplate(var1, GIOPVersion.V1_0, var6);
      IORTemplate var8 = IORFactories.makeIORTemplate(var5.getTemplate());
      var8.add(var7);
      IOR var9 = var8.makeIOR(var1, "", var5.getId());
      this.bootstrapDelegate = ORBUtility.makeClientDelegate(var9);
   }

   private InputStream invoke(String var1, String var2) {
      boolean var3 = true;
      InputStream var4 = null;

      while(var3) {
         Object var5 = null;
         var3 = false;
         OutputStream var6 = this.bootstrapDelegate.request((org.omg.CORBA.Object)var5, var1, true);
         if (var2 != null) {
            var6.write_string(var2);
         }

         try {
            var4 = this.bootstrapDelegate.invoke((org.omg.CORBA.Object)var5, var6);
         } catch (ApplicationException var8) {
            throw this.wrapper.bootstrapApplicationException((Throwable)var8);
         } catch (RemarshalException var9) {
            var3 = true;
         }
      }

      return var4;
   }

   public org.omg.CORBA.Object resolve(String var1) {
      InputStream var2 = null;
      org.omg.CORBA.Object var3 = null;

      try {
         var2 = this.invoke("get", var1);
         var3 = var2.read_Object();
      } finally {
         this.bootstrapDelegate.releaseReply((org.omg.CORBA.Object)null, var2);
      }

      return var3;
   }

   public Set list() {
      InputStream var1 = null;
      HashSet var2 = new HashSet();

      try {
         var1 = this.invoke("list", (String)null);
         int var3 = var1.read_long();

         for(int var4 = 0; var4 < var3; ++var4) {
            var2.add(var1.read_string());
         }
      } finally {
         this.bootstrapDelegate.releaseReply((org.omg.CORBA.Object)null, var1);
      }

      return var2;
   }
}
