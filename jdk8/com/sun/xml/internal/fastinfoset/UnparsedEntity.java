package com.sun.xml.internal.fastinfoset;

public class UnparsedEntity extends Notation {
   public final String notationName;

   public UnparsedEntity(String _name, String _systemIdentifier, String _publicIdentifier, String _notationName) {
      super(_name, _systemIdentifier, _publicIdentifier);
      this.notationName = _notationName;
   }
}
