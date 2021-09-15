package sun.security.jgss.wrapper;

import java.util.Hashtable;
import org.ietf.jgss.ChannelBinding;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import org.ietf.jgss.Oid;

class GSSLibStub {
   private Oid mech;
   private long pMech;
   private static Hashtable<Oid, GSSLibStub> table = new Hashtable(5);

   static native boolean init(String var0, boolean var1);

   private static native long getMechPtr(byte[] var0);

   static native Oid[] indicateMechs();

   native Oid[] inquireNamesForMech() throws GSSException;

   native void releaseName(long var1);

   native long importName(byte[] var1, Oid var2);

   native boolean compareName(long var1, long var3);

   native long canonicalizeName(long var1);

   native byte[] exportName(long var1) throws GSSException;

   native Object[] displayName(long var1) throws GSSException;

   native long acquireCred(long var1, int var3, int var4) throws GSSException;

   native long releaseCred(long var1);

   native long getCredName(long var1);

   native int getCredTime(long var1);

   native int getCredUsage(long var1);

   native NativeGSSContext importContext(byte[] var1);

   native byte[] initContext(long var1, long var3, ChannelBinding var5, byte[] var6, NativeGSSContext var7);

   native byte[] acceptContext(long var1, ChannelBinding var3, byte[] var4, NativeGSSContext var5);

   native long[] inquireContext(long var1);

   native Oid getContextMech(long var1);

   native long getContextName(long var1, boolean var3);

   native int getContextTime(long var1);

   native long deleteContext(long var1);

   native int wrapSizeLimit(long var1, int var3, int var4, int var5);

   native byte[] exportContext(long var1);

   native byte[] getMic(long var1, int var3, byte[] var4);

   native void verifyMic(long var1, byte[] var3, byte[] var4, MessageProp var5);

   native byte[] wrap(long var1, byte[] var3, MessageProp var4);

   native byte[] unwrap(long var1, byte[] var3, MessageProp var4);

   static GSSLibStub getInstance(Oid var0) throws GSSException {
      GSSLibStub var1 = (GSSLibStub)table.get(var0);
      if (var1 == null) {
         var1 = new GSSLibStub(var0);
         table.put(var0, var1);
      }

      return var1;
   }

   private GSSLibStub(Oid var1) throws GSSException {
      SunNativeProvider.debug("Created GSSLibStub for mech " + var1);
      this.mech = var1;
      this.pMech = getMechPtr(var1.getDER());
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof GSSLibStub) ? false : this.mech.equals(((GSSLibStub)var1).getMech());
      }
   }

   public int hashCode() {
      return this.mech.hashCode();
   }

   Oid getMech() {
      return this.mech;
   }
}
