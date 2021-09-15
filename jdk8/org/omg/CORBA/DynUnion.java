package org.omg.CORBA;

/** @deprecated */
@Deprecated
public interface DynUnion extends Object, DynAny {
   boolean set_as_default();

   void set_as_default(boolean var1);

   DynAny discriminator();

   TCKind discriminator_kind();

   DynAny member();

   String member_name();

   void member_name(String var1);

   TCKind member_kind();
}
