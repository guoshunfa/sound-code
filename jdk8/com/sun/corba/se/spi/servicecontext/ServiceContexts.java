package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;

public class ServiceContexts {
   private static final int JAVAIDL_ALIGN_SERVICE_ID = -1106033203;
   private ORB orb;
   private Map scMap;
   private boolean addAlignmentOnWrite;
   private CodeBase codeBase;
   private GIOPVersion giopVersion;
   private ORBUtilSystemException wrapper;

   private static boolean isDebugging(OutputStream var0) {
      ORB var1 = (ORB)((ORB)var0.orb());
      return var1 == null ? false : var1.serviceContextDebugFlag;
   }

   private static boolean isDebugging(InputStream var0) {
      ORB var1 = (ORB)((ORB)var0.orb());
      return var1 == null ? false : var1.serviceContextDebugFlag;
   }

   private void dprint(String var1) {
      ORBUtility.dprint((Object)this, var1);
   }

   public static void writeNullServiceContext(OutputStream var0) {
      if (isDebugging(var0)) {
         ORBUtility.dprint("ServiceContexts", "Writing null service context");
      }

      var0.write_long(0);
   }

   private void createMapFromInputStream(InputStream var1) {
      this.orb = (ORB)((ORB)var1.orb());
      if (this.orb.serviceContextDebugFlag) {
         this.dprint("Constructing ServiceContexts from input stream");
      }

      int var2 = var1.read_long();
      if (this.orb.serviceContextDebugFlag) {
         this.dprint("Number of service contexts = " + var2);
      }

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.read_long();
         if (this.orb.serviceContextDebugFlag) {
            this.dprint("Reading service context id " + var4);
         }

         byte[] var5 = OctetSeqHelper.read(var1);
         if (this.orb.serviceContextDebugFlag) {
            this.dprint("Service context" + var4 + " length: " + var5.length);
         }

         this.scMap.put(new Integer(var4), var5);
      }

   }

   public ServiceContexts(ORB var1) {
      this.orb = var1;
      this.wrapper = ORBUtilSystemException.get(var1, "rpc.protocol");
      this.addAlignmentOnWrite = false;
      this.scMap = new HashMap();
      this.giopVersion = var1.getORBData().getGIOPVersion();
      this.codeBase = null;
   }

   public ServiceContexts(InputStream var1) {
      this((ORB)((ORB)var1.orb()));
      this.codeBase = ((CDRInputStream)var1).getCodeBase();
      this.createMapFromInputStream(var1);
      this.giopVersion = ((CDRInputStream)var1).getGIOPVersion();
   }

   private ServiceContext unmarshal(Integer var1, byte[] var2) {
      ServiceContextRegistry var3 = this.orb.getServiceContextRegistry();
      ServiceContextData var4 = var3.findServiceContextData(var1);
      Object var5 = null;
      if (var4 == null) {
         if (this.orb.serviceContextDebugFlag) {
            this.dprint("Could not find ServiceContextData for " + var1 + " using UnknownServiceContext");
         }

         var5 = new UnknownServiceContext(var1, var2);
      } else {
         if (this.orb.serviceContextDebugFlag) {
            this.dprint("Found " + var4);
         }

         EncapsInputStream var6 = EncapsInputStreamFactory.newEncapsInputStream(this.orb, var2, var2.length, this.giopVersion, this.codeBase);
         var6.consumeEndian();
         var5 = var4.makeServiceContext(var6, this.giopVersion);
         if (var5 == null) {
            throw this.wrapper.svcctxUnmarshalError(CompletionStatus.COMPLETED_MAYBE);
         }
      }

      return (ServiceContext)var5;
   }

   public void addAlignmentPadding() {
      this.addAlignmentOnWrite = true;
   }

   public void write(OutputStream var1, GIOPVersion var2) {
      if (isDebugging(var1)) {
         this.dprint("Writing service contexts to output stream");
         Utility.printStackTrace();
      }

      int var3 = this.scMap.size();
      if (this.addAlignmentOnWrite) {
         if (isDebugging(var1)) {
            this.dprint("Adding alignment padding");
         }

         ++var3;
      }

      if (isDebugging(var1)) {
         this.dprint("Service context has " + var3 + " components");
      }

      var1.write_long(var3);
      this.writeServiceContextsInOrder(var1, var2);
      if (this.addAlignmentOnWrite) {
         if (isDebugging(var1)) {
            this.dprint("Writing alignment padding");
         }

         var1.write_long(-1106033203);
         var1.write_long(4);
         var1.write_octet((byte)0);
         var1.write_octet((byte)0);
         var1.write_octet((byte)0);
         var1.write_octet((byte)0);
      }

      if (isDebugging(var1)) {
         this.dprint("Service context writing complete");
      }

   }

   private void writeServiceContextsInOrder(OutputStream var1, GIOPVersion var2) {
      Integer var3 = new Integer(9);
      Object var4 = this.scMap.remove(var3);
      Iterator var5 = this.scMap.keySet().iterator();

      while(var5.hasNext()) {
         Integer var6 = (Integer)var5.next();
         this.writeMapEntry(var1, var6, this.scMap.get(var6), var2);
      }

      if (var4 != null) {
         this.writeMapEntry(var1, var3, var4, var2);
         this.scMap.put(var3, var4);
      }

   }

   private void writeMapEntry(OutputStream var1, Integer var2, Object var3, GIOPVersion var4) {
      if (var3 instanceof byte[]) {
         if (isDebugging(var1)) {
            this.dprint("Writing service context bytes for id " + var2);
         }

         OctetSeqHelper.write(var1, (byte[])((byte[])var3));
      } else {
         ServiceContext var5 = (ServiceContext)var3;
         if (isDebugging(var1)) {
            this.dprint("Writing service context " + var5);
         }

         var5.write(var1, var4);
      }

   }

   public void put(ServiceContext var1) {
      Integer var2 = new Integer(var1.getId());
      this.scMap.put(var2, var1);
   }

   public void delete(int var1) {
      this.delete(new Integer(var1));
   }

   public void delete(Integer var1) {
      this.scMap.remove(var1);
   }

   public ServiceContext get(int var1) {
      return this.get(new Integer(var1));
   }

   public ServiceContext get(Integer var1) {
      Object var2 = this.scMap.get(var1);
      if (var2 == null) {
         return null;
      } else if (var2 instanceof byte[]) {
         ServiceContext var3 = this.unmarshal(var1, (byte[])((byte[])var2));
         this.scMap.put(var1, var3);
         return var3;
      } else {
         return (ServiceContext)var2;
      }
   }
}
