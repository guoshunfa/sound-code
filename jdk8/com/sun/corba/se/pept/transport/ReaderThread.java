package com.sun.corba.se.pept.transport;

public interface ReaderThread {
   Connection getConnection();

   void close();
}
