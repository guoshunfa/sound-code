package com.sun.corba.se.impl.javax.rmi.CORBA;

class KeepAlive extends Thread {
   boolean quit = false;

   public KeepAlive() {
      this.setDaemon(false);
   }

   public synchronized void run() {
      while(!this.quit) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }

   }

   public synchronized void quit() {
      this.quit = true;
      this.notifyAll();
   }
}
