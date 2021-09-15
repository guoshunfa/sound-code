package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.TaggedProfile;
import com.sun.corba.se.spi.ior.TaggedProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.IOP.TaggedProfileHelper;
import sun.corba.OutputStreamFactory;

public class GenericTaggedProfile extends GenericIdentifiable implements TaggedProfile {
   private ORB orb;

   public GenericTaggedProfile(int var1, InputStream var2) {
      super(var1, var2);
      this.orb = (ORB)((ORB)var2.orb());
   }

   public GenericTaggedProfile(ORB var1, int var2, byte[] var3) {
      super(var2, var3);
      this.orb = var1;
   }

   public TaggedProfileTemplate getTaggedProfileTemplate() {
      return null;
   }

   public ObjectId getObjectId() {
      return null;
   }

   public ObjectKeyTemplate getObjectKeyTemplate() {
      return null;
   }

   public ObjectKey getObjectKey() {
      return null;
   }

   public boolean isEquivalent(TaggedProfile var1) {
      return this.equals(var1);
   }

   public void makeImmutable() {
   }

   public boolean isLocal() {
      return false;
   }

   public org.omg.IOP.TaggedProfile getIOPProfile() {
      EncapsOutputStream var1 = OutputStreamFactory.newEncapsOutputStream(this.orb);
      this.write(var1);
      InputStream var2 = (InputStream)((InputStream)var1.create_input_stream());
      return TaggedProfileHelper.read(var2);
   }
}
