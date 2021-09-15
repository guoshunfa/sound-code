package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.impl.orbutil.HexOutputStream;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import org.omg.IOP.IORHelper;
import sun.corba.OutputStreamFactory;

public class IORImpl extends IdentifiableContainerBase implements IOR {
   private String typeId;
   private ORB factory;
   private boolean isCachedHashValue;
   private int cachedHashValue;
   IORSystemException wrapper;
   private IORTemplateList iortemps;

   public ORB getORB() {
      return this.factory;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof IOR)) {
         return false;
      } else {
         IOR var2 = (IOR)var1;
         return super.equals(var1) && this.typeId.equals(var2.getTypeId());
      }
   }

   public synchronized int hashCode() {
      if (!this.isCachedHashValue) {
         this.cachedHashValue = super.hashCode() ^ this.typeId.hashCode();
         this.isCachedHashValue = true;
      }

      return this.cachedHashValue;
   }

   public IORImpl(ORB var1) {
      this(var1, "");
   }

   public IORImpl(ORB var1, String var2) {
      this.factory = null;
      this.isCachedHashValue = false;
      this.iortemps = null;
      this.factory = var1;
      this.wrapper = IORSystemException.get(var1, "oa.ior");
      this.typeId = var2;
   }

   public IORImpl(ORB var1, String var2, IORTemplate var3, ObjectId var4) {
      this(var1, var2);
      this.iortemps = IORFactories.makeIORTemplateList();
      this.iortemps.add(var3);
      this.addTaggedProfiles(var3, var4);
      this.makeImmutable();
   }

   private void addTaggedProfiles(IORTemplate var1, ObjectId var2) {
      ObjectKeyTemplate var3 = var1.getObjectKeyTemplate();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         TaggedProfileTemplate var5 = (TaggedProfileTemplate)((TaggedProfileTemplate)var4.next());
         TaggedProfile var6 = var5.create(var3, var2);
         this.add(var6);
      }

   }

   public IORImpl(ORB var1, String var2, IORTemplateList var3, ObjectId var4) {
      this(var1, var2);
      this.iortemps = var3;
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         IORTemplate var6 = (IORTemplate)((IORTemplate)var5.next());
         this.addTaggedProfiles(var6, var4);
      }

      this.makeImmutable();
   }

   public IORImpl(InputStream var1) {
      this((ORB)((ORB)var1.orb()), var1.read_string());
      IdentifiableFactoryFinder var2 = this.factory.getTaggedProfileFactoryFinder();
      EncapsulationUtility.readIdentifiableSequence(this, var2, var1);
      this.makeImmutable();
   }

   public String getTypeId() {
      return this.typeId;
   }

   public void write(OutputStream var1) {
      var1.write_string(this.typeId);
      EncapsulationUtility.writeIdentifiableSequence(this, var1);
   }

   public String stringify() {
      EncapsOutputStream var2 = OutputStreamFactory.newEncapsOutputStream(this.factory);
      var2.putEndian();
      this.write((OutputStream)var2);
      StringWriter var1 = new StringWriter();

      try {
         var2.writeTo(new HexOutputStream(var1));
      } catch (IOException var4) {
         throw this.wrapper.stringifyWriteError((Throwable)var4);
      }

      return "IOR:" + var1;
   }

   public synchronized void makeImmutable() {
      this.makeElementsImmutable();
      if (this.iortemps != null) {
         this.iortemps.makeImmutable();
      }

      super.makeImmutable();
   }

   public org.omg.IOP.IOR getIOPIOR() {
      EncapsOutputStream var1 = OutputStreamFactory.newEncapsOutputStream(this.factory);
      this.write(var1);
      InputStream var2 = (InputStream)((InputStream)var1.create_input_stream());
      return IORHelper.read(var2);
   }

   public boolean isNil() {
      return this.size() == 0;
   }

   public boolean isEquivalent(IOR var1) {
      Iterator var2 = this.iterator();
      Iterator var3 = var1.iterator();

      while(var2.hasNext() && var3.hasNext()) {
         TaggedProfile var4 = (TaggedProfile)((TaggedProfile)var2.next());
         TaggedProfile var5 = (TaggedProfile)((TaggedProfile)var3.next());
         if (!var4.isEquivalent(var5)) {
            return false;
         }
      }

      return var2.hasNext() == var3.hasNext();
   }

   private void initializeIORTemplateList() {
      HashMap var1 = new HashMap();
      this.iortemps = IORFactories.makeIORTemplateList();
      Iterator var2 = this.iterator();

      TaggedProfileTemplate var5;
      IORTemplate var7;
      for(ObjectId var3 = null; var2.hasNext(); var7.add(var5)) {
         TaggedProfile var4 = (TaggedProfile)((TaggedProfile)var2.next());
         var5 = var4.getTaggedProfileTemplate();
         ObjectKeyTemplate var6 = var4.getObjectKeyTemplate();
         if (var3 == null) {
            var3 = var4.getObjectId();
         } else if (!var3.equals(var4.getObjectId())) {
            throw this.wrapper.badOidInIorTemplateList();
         }

         var7 = (IORTemplate)((IORTemplate)var1.get(var6));
         if (var7 == null) {
            var7 = IORFactories.makeIORTemplate(var6);
            var1.put(var6, var7);
            this.iortemps.add(var7);
         }
      }

      this.iortemps.makeImmutable();
   }

   public synchronized IORTemplateList getIORTemplates() {
      if (this.iortemps == null) {
         this.initializeIORTemplateList();
      }

      return this.iortemps;
   }

   public IIOPProfile getProfile() {
      IIOPProfile var1 = null;
      Iterator var2 = this.iteratorById(0);
      if (var2.hasNext()) {
         var1 = (IIOPProfile)((IIOPProfile)var2.next());
      }

      if (var1 != null) {
         return var1;
      } else {
         throw this.wrapper.iorMustHaveIiopProfile();
      }
   }
}
