package org.omg.CORBA_2_3.portable;

public abstract class ObjectImpl extends org.omg.CORBA.portable.ObjectImpl {
   public String _get_codebase() {
      org.omg.CORBA.portable.Delegate var1 = this._get_delegate();
      return var1 instanceof Delegate ? ((Delegate)var1).get_codebase(this) : null;
   }
}
