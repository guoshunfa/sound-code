package com.sun.jndi.dns;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class Tcp {
   private Socket sock;
   InputStream in;
   OutputStream out;

   Tcp(InetAddress var1, int var2) throws IOException {
      this.sock = new Socket(var1, var2);
      this.sock.setTcpNoDelay(true);
      this.out = new BufferedOutputStream(this.sock.getOutputStream());
      this.in = new BufferedInputStream(this.sock.getInputStream());
   }

   void close() throws IOException {
      this.sock.close();
   }
}
