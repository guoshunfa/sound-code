package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IdentifiableBase;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import java.util.Iterator;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class IIOPProfileImpl extends IdentifiableBase implements IIOPProfile {
   private ORB orb;
   private IORSystemException wrapper;
   private ObjectId oid;
   private IIOPProfileTemplate proftemp;
   private ObjectKeyTemplate oktemp;
   protected String codebase;
   protected boolean cachedCodebase;
   private boolean checkedIsLocal;
   private boolean cachedIsLocal;
   private GIOPVersion giopVersion;

   public boolean equals(Object var1) {
      if (!(var1 instanceof IIOPProfileImpl)) {
         return false;
      } else {
         IIOPProfileImpl var2 = (IIOPProfileImpl)var1;
         return this.oid.equals(var2.oid) && this.proftemp.equals(var2.proftemp) && this.oktemp.equals(var2.oktemp);
      }
   }

   public int hashCode() {
      return this.oid.hashCode() ^ this.proftemp.hashCode() ^ this.oktemp.hashCode();
   }

   public ObjectId getObjectId() {
      return this.oid;
   }

   public TaggedProfileTemplate getTaggedProfileTemplate() {
      return this.proftemp;
   }

   public ObjectKeyTemplate getObjectKeyTemplate() {
      return this.oktemp;
   }

   private IIOPProfileImpl(ORB var1) {
      this.codebase = null;
      this.cachedCodebase = false;
      this.checkedIsLocal = false;
      this.cachedIsLocal = false;
      this.giopVersion = null;
      this.orb = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
   }

   public IIOPProfileImpl(ORB var1, ObjectKeyTemplate var2, ObjectId var3, IIOPProfileTemplate var4) {
      this(var1);
      this.oktemp = var2;
      this.oid = var3;
      this.proftemp = var4;
   }

   public IIOPProfileImpl(InputStream var1) {
      this((ORB)((ORB)var1.orb()));
      this.init(var1);
   }

   public IIOPProfileImpl(ORB var1, TaggedProfile var2) {
      this(var1);
      if (var2 != null && var2.tag == 0 && var2.profile_data != null) {
         EncapsInputStream var3 = EncapsInputStreamFactory.newEncapsInputStream(var1, var2.profile_data, var2.profile_data.length);
         var3.consumeEndian();
         this.init(var3);
      } else {
         throw this.wrapper.invalidTaggedProfile();
      }
   }

   private void init(InputStream var1) {
      GIOPVersion var2 = new GIOPVersion();
      var2.read(var1);
      IIOPAddressImpl var3 = new IIOPAddressImpl(var1);
      byte[] var4 = EncapsulationUtility.readOctets(var1);
      ObjectKey var5 = this.orb.getObjectKeyFactory().create(var4);
      this.oktemp = var5.getTemplate();
      this.oid = var5.getId();
      this.proftemp = IIOPFactories.makeIIOPProfileTemplate(this.orb, var2, var3);
      if (var2.getMinor() > 0) {
         EncapsulationUtility.readIdentifiableSequence(this.proftemp, this.orb.getTaggedComponentFactoryFinder(), var1);
      }

      if (this.uncachedGetCodeBase() == null) {
         JavaCodebaseComponent var6 = IIOPProfileImpl.LocalCodeBaseSingletonHolder.comp;
         if (var6 != null) {
            if (var2.getMinor() > 0) {
               this.proftemp.add(var6);
            }

            this.codebase = var6.getURLs();
         }

         this.cachedCodebase = true;
      }

   }

   public void writeContents(OutputStream var1) {
      this.proftemp.write(this.oktemp, this.oid, var1);
   }

   public int getId() {
      return this.proftemp.getId();
   }

   public boolean isEquivalent(com.sun.corba.se.spi.ior.TaggedProfile var1) {
      if (!(var1 instanceof IIOPProfile)) {
         return false;
      } else {
         IIOPProfile var2 = (IIOPProfile)var1;
         return this.oid.equals(var2.getObjectId()) && this.proftemp.isEquivalent(var2.getTaggedProfileTemplate()) && this.oktemp.equals(var2.getObjectKeyTemplate());
      }
   }

   public ObjectKey getObjectKey() {
      ObjectKey var1 = IORFactories.makeObjectKey(this.oktemp, this.oid);
      return var1;
   }

   public TaggedProfile getIOPProfile() {
      EncapsOutputStream var1 = OutputStreamFactory.newEncapsOutputStream(this.orb);
      var1.write_long(this.getId());
      this.write(var1);
      InputStream var2 = (InputStream)((InputStream)var1.create_input_stream());
      return TaggedProfileHelper.read(var2);
   }

   private String uncachedGetCodeBase() {
      Iterator var1 = this.proftemp.iteratorById(25);
      if (var1.hasNext()) {
         JavaCodebaseComponent var2 = (JavaCodebaseComponent)((JavaCodebaseComponent)var1.next());
         return var2.getURLs();
      } else {
         return null;
      }
   }

   public synchronized String getCodebase() {
      if (!this.cachedCodebase) {
         this.cachedCodebase = true;
         this.codebase = this.uncachedGetCodeBase();
      }

      return this.codebase;
   }

   public ORBVersion getORBVersion() {
      return this.oktemp.getORBVersion();
   }

   public synchronized boolean isLocal() {
      if (!this.checkedIsLocal) {
         this.checkedIsLocal = true;
         String var1 = this.proftemp.getPrimaryAddress().getHost();
         this.cachedIsLocal = this.orb.isLocalHost(var1) && this.orb.isLocalServerId(this.oktemp.getSubcontractId(), this.oktemp.getServerId()) && this.orb.getLegacyServerSocketManager().legacyIsLocalServerPort(this.proftemp.getPrimaryAddress().getPort());
      }

      return this.cachedIsLocal;
   }

   public Object getServant() {
      if (!this.isLocal()) {
         return null;
      } else {
         RequestDispatcherRegistry var1 = this.orb.getRequestDispatcherRegistry();
         ObjectAdapterFactory var2 = var1.getObjectAdapterFactory(this.oktemp.getSubcontractId());
         ObjectAdapterId var3 = this.oktemp.getObjectAdapterId();
         ObjectAdapter var4 = null;

         try {
            var4 = var2.find(var3);
         } catch (SystemException var7) {
            this.wrapper.getLocalServantFailure((Throwable)var7, var3.toString());
            return null;
         }

         byte[] var5 = this.oid.getId();
         org.omg.CORBA.Object var6 = var4.getLocalServant(var5);
         return var6;
      }
   }

   public synchronized GIOPVersion getGIOPVersion() {
      return this.proftemp.getGIOPVersion();
   }

   public void makeImmutable() {
      this.proftemp.makeImmutable();
   }

   private static class LocalCodeBaseSingletonHolder {
      public static JavaCodebaseComponent comp;

      static {
         String var0 = JDKBridge.getLocalCodebase();
         if (var0 == null) {
            comp = null;
         } else {
            comp = IIOPFactories.makeJavaCodebaseComponent(var0);
         }

      }
   }
}
