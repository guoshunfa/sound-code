package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORFactory;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.IORTemplateList;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.orb.ORB;
import java.util.ArrayList;
import java.util.Iterator;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class IORTemplateListImpl extends FreezableList implements IORTemplateList {
   public Object set(int var1, Object var2) {
      if (var2 instanceof IORTemplate) {
         return super.set(var1, var2);
      } else if (var2 instanceof IORTemplateList) {
         Object var3 = this.remove(var1);
         this.add(var1, var2);
         return var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void add(int var1, Object var2) {
      if (var2 instanceof IORTemplate) {
         super.add(var1, var2);
      } else {
         if (!(var2 instanceof IORTemplateList)) {
            throw new IllegalArgumentException();
         }

         IORTemplateList var3 = (IORTemplateList)var2;
         this.addAll(var1, var3);
      }

   }

   public IORTemplateListImpl() {
      super(new ArrayList());
   }

   public IORTemplateListImpl(InputStream var1) {
      this();
      int var2 = var1.read_long();

      for(int var3 = 0; var3 < var2; ++var3) {
         IORTemplate var4 = IORFactories.makeIORTemplate(var1);
         this.add(var4);
      }

      this.makeImmutable();
   }

   public void makeImmutable() {
      this.makeElementsImmutable();
      super.makeImmutable();
   }

   public void write(OutputStream var1) {
      var1.write_long(this.size());
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         IORTemplate var3 = (IORTemplate)((IORTemplate)var2.next());
         var3.write(var1);
      }

   }

   public IOR makeIOR(ORB var1, String var2, ObjectId var3) {
      return new IORImpl(var1, var2, this, var3);
   }

   public boolean isEquivalent(IORFactory var1) {
      if (!(var1 instanceof IORTemplateList)) {
         return false;
      } else {
         IORTemplateList var2 = (IORTemplateList)var1;
         Iterator var3 = this.iterator();
         Iterator var4 = var2.iterator();

         while(var3.hasNext() && var4.hasNext()) {
            IORTemplate var5 = (IORTemplate)var3.next();
            IORTemplate var6 = (IORTemplate)var4.next();
            if (!var5.isEquivalent(var6)) {
               return false;
            }
         }

         return var3.hasNext() == var4.hasNext();
      }
   }
}
