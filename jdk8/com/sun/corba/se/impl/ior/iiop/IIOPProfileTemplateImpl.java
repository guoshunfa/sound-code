package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.ior.EncapsulationUtility;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplateBase;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class IIOPProfileTemplateImpl extends TaggedProfileTemplateBase implements IIOPProfileTemplate {
   private ORB orb;
   private GIOPVersion giopVersion;
   private IIOPAddress primary;

   public boolean equals(Object var1) {
      if (!(var1 instanceof IIOPProfileTemplateImpl)) {
         return false;
      } else {
         IIOPProfileTemplateImpl var2 = (IIOPProfileTemplateImpl)var1;
         return super.equals(var1) && this.giopVersion.equals(var2.giopVersion) && this.primary.equals(var2.primary);
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.giopVersion.hashCode() ^ this.primary.hashCode();
   }

   public TaggedProfile create(ObjectKeyTemplate var1, ObjectId var2) {
      return IIOPFactories.makeIIOPProfile(this.orb, var1, var2, this);
   }

   public GIOPVersion getGIOPVersion() {
      return this.giopVersion;
   }

   public IIOPAddress getPrimaryAddress() {
      return this.primary;
   }

   public IIOPProfileTemplateImpl(ORB var1, GIOPVersion var2, IIOPAddress var3) {
      this.orb = var1;
      this.giopVersion = var2;
      this.primary = var3;
      if (this.giopVersion.getMinor() == 0) {
         this.makeImmutable();
      }

   }

   public IIOPProfileTemplateImpl(InputStream var1) {
      byte var2 = var1.read_octet();
      byte var3 = var1.read_octet();
      this.giopVersion = GIOPVersion.getInstance(var2, var3);
      this.primary = new IIOPAddressImpl(var1);
      this.orb = (ORB)((ORB)var1.orb());
      if (var3 > 0) {
         EncapsulationUtility.readIdentifiableSequence(this, this.orb.getTaggedComponentFactoryFinder(), var1);
      }

      this.makeImmutable();
   }

   public void write(ObjectKeyTemplate var1, ObjectId var2, OutputStream var3) {
      this.giopVersion.write(var3);
      this.primary.write(var3);
      EncapsOutputStream var4 = OutputStreamFactory.newEncapsOutputStream((ORB)var3.orb(), ((CDROutputStream)var3).isLittleEndian());
      var1.write(var2, var4);
      EncapsulationUtility.writeOutputStream(var4, var3);
      if (this.giopVersion.getMinor() > 0) {
         EncapsulationUtility.writeIdentifiableSequence(this, var3);
      }

   }

   public void writeContents(OutputStream var1) {
      this.giopVersion.write(var1);
      this.primary.write(var1);
      if (this.giopVersion.getMinor() > 0) {
         EncapsulationUtility.writeIdentifiableSequence(this, var1);
      }

   }

   public int getId() {
      return 0;
   }

   public boolean isEquivalent(TaggedProfileTemplate var1) {
      if (!(var1 instanceof IIOPProfileTemplateImpl)) {
         return false;
      } else {
         IIOPProfileTemplateImpl var2 = (IIOPProfileTemplateImpl)var1;
         return this.primary.equals(var2.primary);
      }
   }
}
