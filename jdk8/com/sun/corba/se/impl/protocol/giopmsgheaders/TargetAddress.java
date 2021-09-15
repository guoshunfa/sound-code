package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.TaggedProfile;

public final class TargetAddress implements IDLEntity {
   private byte[] ___object_key;
   private TaggedProfile ___profile;
   private IORAddressingInfo ___ior;
   private short __discriminator;
   private boolean __uninitialized = true;

   public short discriminator() {
      if (this.__uninitialized) {
         throw new BAD_OPERATION();
      } else {
         return this.__discriminator;
      }
   }

   public byte[] object_key() {
      if (this.__uninitialized) {
         throw new BAD_OPERATION();
      } else {
         this.verifyobject_key(this.__discriminator);
         return this.___object_key;
      }
   }

   public void object_key(byte[] var1) {
      this.__discriminator = 0;
      this.___object_key = var1;
      this.__uninitialized = false;
   }

   private void verifyobject_key(short var1) {
      if (var1 != 0) {
         throw new BAD_OPERATION();
      }
   }

   public TaggedProfile profile() {
      if (this.__uninitialized) {
         throw new BAD_OPERATION();
      } else {
         this.verifyprofile(this.__discriminator);
         return this.___profile;
      }
   }

   public void profile(TaggedProfile var1) {
      this.__discriminator = 1;
      this.___profile = var1;
      this.__uninitialized = false;
   }

   private void verifyprofile(short var1) {
      if (var1 != 1) {
         throw new BAD_OPERATION();
      }
   }

   public IORAddressingInfo ior() {
      if (this.__uninitialized) {
         throw new BAD_OPERATION();
      } else {
         this.verifyior(this.__discriminator);
         return this.___ior;
      }
   }

   public void ior(IORAddressingInfo var1) {
      this.__discriminator = 2;
      this.___ior = var1;
      this.__uninitialized = false;
   }

   private void verifyior(short var1) {
      if (var1 != 2) {
         throw new BAD_OPERATION();
      }
   }

   public void _default() {
      this.__discriminator = -32768;
      this.__uninitialized = false;
   }
}
