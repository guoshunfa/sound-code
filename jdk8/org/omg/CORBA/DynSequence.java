package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.InvalidSeq;

/** @deprecated */
@Deprecated
public interface DynSequence extends Object, DynAny {
   int length();

   void length(int var1);

   Any[] get_elements();

   void set_elements(Any[] var1) throws InvalidSeq;
}
