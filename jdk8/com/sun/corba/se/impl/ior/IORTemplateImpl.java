package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IdentifiableContainerBase;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateImpl extends IdentifiableContainerBase implements IORTemplate {
   private ObjectKeyTemplate oktemp;

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof IORTemplateImpl)) {
         return false;
      } else {
         IORTemplateImpl var2 = (IORTemplateImpl)var1;
         return super.equals(var1) && this.oktemp.equals(var2.getObjectKeyTemplate());
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.oktemp.hashCode();
   }

   public ObjectKeyTemplate getObjectKeyTemplate() {
      return this.oktemp;
   }

   public IORTemplateImpl(ObjectKeyTemplate var1) {
      this.oktemp = var1;
   }

   public IOR makeIOR(ORB var1, String var2, ObjectId var3) {
      return new IORImpl(var1, var2, this, var3);
   }

   public boolean isEquivalent(IORFactory var1) {
      if (!(var1 instanceof IORTemplate)) {
         return false;
      } else {
         IORTemplate var2 = (IORTemplate)var1;
         Iterator var3 = this.iterator();
         Iterator var4 = var2.iterator();

         while(var3.hasNext() && var4.hasNext()) {
            TaggedProfileTemplate var5 = (TaggedProfileTemplate)var3.next();
            TaggedProfileTemplate var6 = (TaggedProfileTemplate)var4.next();
            if (!var5.isEquivalent(var6)) {
               return false;
            }
         }

         return var3.hasNext() == var4.hasNext() && this.getObjectKeyTemplate().equals(var2.getObjectKeyTemplate());
      }
   }

   public void makeImmutable() {
      this.makeElementsImmutable();
      super.makeImmutable();
   }

   public void write(OutputStream var1) {
      this.oktemp.write(var1);
      EncapsulationUtility.writeIdentifiableSequence(this, var1);
   }

   public IORTemplateImpl(InputStream var1) {
      ORB var2 = (ORB)((ORB)var1.orb());
      IdentifiableFactoryFinder var3 = var2.getTaggedProfileTemplateFactoryFinder();
      this.oktemp = var2.getObjectKeyFactory().createTemplate(var1);
      EncapsulationUtility.readIdentifiableSequence(this, var3, var1);
      this.makeImmutable();
   }
}
