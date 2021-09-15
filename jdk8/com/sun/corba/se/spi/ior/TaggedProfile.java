package com.sun.corba.se.spi.ior;

public interface TaggedProfile extends Identifiable, MakeImmutable {
   TaggedProfileTemplate getTaggedProfileTemplate();

   ObjectId getObjectId();

   ObjectKeyTemplate getObjectKeyTemplate();

   ObjectKey getObjectKey();

   boolean isEquivalent(TaggedProfile var1);

   org.omg.IOP.TaggedProfile getIOPProfile();

   boolean isLocal();
}
