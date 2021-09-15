package com.sun.corba.se.impl.orbutil;

public abstract class RepositoryIdFactory {
   private static final RepIdDelegator currentDelegator = new RepIdDelegator();

   public static RepositoryIdStrings getRepIdStringsFactory() {
      return currentDelegator;
   }

   public static RepositoryIdUtility getRepIdUtility() {
      return currentDelegator;
   }
}
