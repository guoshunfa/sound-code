package com.sun.org.apache.xml.internal.resolver;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogEntry {
   protected static AtomicInteger nextEntry = new AtomicInteger(0);
   protected static final Map<String, Integer> entryTypes = new ConcurrentHashMap();
   protected static Vector entryArgs = new Vector();
   protected int entryType = 0;
   protected Vector args = null;

   static int addEntryType(String name, int numArgs) {
      int index = nextEntry.getAndIncrement();
      entryTypes.put(name, index);
      entryArgs.add(index, numArgs);
      return index;
   }

   public static int getEntryType(String name) throws CatalogException {
      if (!entryTypes.containsKey(name)) {
         throw new CatalogException(3);
      } else {
         Integer iType = (Integer)entryTypes.get(name);
         if (iType == null) {
            throw new CatalogException(3);
         } else {
            return iType;
         }
      }
   }

   public static int getEntryArgCount(String name) throws CatalogException {
      return getEntryArgCount(getEntryType(name));
   }

   public static int getEntryArgCount(int type) throws CatalogException {
      try {
         Integer iArgs = (Integer)entryArgs.get(type);
         return iArgs;
      } catch (ArrayIndexOutOfBoundsException var2) {
         throw new CatalogException(3);
      }
   }

   public CatalogEntry() {
   }

   public CatalogEntry(String name, Vector args) throws CatalogException {
      Integer iType = (Integer)entryTypes.get(name);
      if (iType == null) {
         throw new CatalogException(3);
      } else {
         int type = iType;

         try {
            Integer iArgs = (Integer)entryArgs.get(type);
            if (iArgs != args.size()) {
               throw new CatalogException(2);
            }
         } catch (ArrayIndexOutOfBoundsException var6) {
            throw new CatalogException(3);
         }

         this.entryType = type;
         this.args = args;
      }
   }

   public CatalogEntry(int type, Vector args) throws CatalogException {
      try {
         Integer iArgs = (Integer)entryArgs.get(type);
         if (iArgs != args.size()) {
            throw new CatalogException(2);
         }
      } catch (ArrayIndexOutOfBoundsException var4) {
         throw new CatalogException(3);
      }

      this.entryType = type;
      this.args = args;
   }

   public int getEntryType() {
      return this.entryType;
   }

   public String getEntryArg(int argNum) {
      try {
         String arg = (String)this.args.get(argNum);
         return arg;
      } catch (ArrayIndexOutOfBoundsException var3) {
         return null;
      }
   }

   public void setEntryArg(int argNum, String newspec) throws ArrayIndexOutOfBoundsException {
      this.args.set(argNum, newspec);
   }
}
