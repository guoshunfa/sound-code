package com.sun.corba.se.impl.orb;

class SynchVariable {
   public boolean _flag = false;

   public void set() {
      this._flag = true;
   }

   public boolean value() {
      return this._flag;
   }

   public void reset() {
      this._flag = false;
   }
}
