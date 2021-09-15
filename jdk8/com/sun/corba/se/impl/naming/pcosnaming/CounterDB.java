package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class CounterDB implements Serializable {
   private Integer counter;
   private static String counterFileName = "counter";
   private transient File counterFile;
   public static final int rootCounter = 0;

   CounterDB(File var1) {
      counterFileName = "counter";
      this.counterFile = new File(var1, counterFileName);
      if (!this.counterFile.exists()) {
         this.counter = new Integer(0);
         this.writeCounter();
      } else {
         this.readCounter();
      }

   }

   private void readCounter() {
      try {
         FileInputStream var1 = new FileInputStream(this.counterFile);
         ObjectInputStream var2 = new ObjectInputStream(var1);
         this.counter = (Integer)var2.readObject();
         var2.close();
      } catch (Exception var3) {
      }

   }

   private void writeCounter() {
      try {
         this.counterFile.delete();
         FileOutputStream var1 = new FileOutputStream(this.counterFile);
         ObjectOutputStream var2 = new ObjectOutputStream(var1);
         var2.writeObject(this.counter);
         var2.flush();
         var2.close();
      } catch (Exception var3) {
      }

   }

   public synchronized int getNextCounter() {
      int var1 = this.counter;
      ++var1;
      this.counter = new Integer(var1);
      this.writeCounter();
      return var1;
   }
}
