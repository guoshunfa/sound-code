package com.oracle.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.sdp.SdpSupport;
import sun.nio.ch.Secrets;

public final class Sdp {
   private static final Constructor<ServerSocket> serverSocketCtor;
   private static final Constructor<SocketImpl> socketImplCtor;

   private Sdp() {
   }

   private static void setAccessible(final AccessibleObject var0) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            var0.setAccessible(true);
            return null;
         }
      });
   }

   private static SocketImpl createSocketImpl() {
      try {
         return (SocketImpl)socketImplCtor.newInstance();
      } catch (InstantiationException var1) {
         throw new AssertionError(var1);
      } catch (IllegalAccessException var2) {
         throw new AssertionError(var2);
      } catch (InvocationTargetException var3) {
         throw new AssertionError(var3);
      }
   }

   public static Socket openSocket() throws IOException {
      SocketImpl var0 = createSocketImpl();
      return new Sdp.SdpSocket(var0);
   }

   public static ServerSocket openServerSocket() throws IOException {
      SocketImpl var0 = createSocketImpl();

      try {
         return (ServerSocket)serverSocketCtor.newInstance(var0);
      } catch (IllegalAccessException var3) {
         throw new AssertionError(var3);
      } catch (InstantiationException var4) {
         throw new AssertionError(var4);
      } catch (InvocationTargetException var5) {
         Throwable var2 = var5.getCause();
         if (var2 instanceof IOException) {
            throw (IOException)var2;
         } else if (var2 instanceof RuntimeException) {
            throw (RuntimeException)var2;
         } else {
            throw new RuntimeException(var5);
         }
      }
   }

   public static SocketChannel openSocketChannel() throws IOException {
      FileDescriptor var0 = SdpSupport.createSocket();
      return Secrets.newSocketChannel(var0);
   }

   public static ServerSocketChannel openServerSocketChannel() throws IOException {
      FileDescriptor var0 = SdpSupport.createSocket();
      return Secrets.newServerSocketChannel(var0);
   }

   static {
      try {
         serverSocketCtor = ServerSocket.class.getDeclaredConstructor(SocketImpl.class);
         setAccessible(serverSocketCtor);
      } catch (NoSuchMethodException var3) {
         throw new AssertionError(var3);
      }

      try {
         Class var0 = Class.forName("java.net.SdpSocketImpl", true, (ClassLoader)null);
         socketImplCtor = var0.getDeclaredConstructor();
         setAccessible(socketImplCtor);
      } catch (ClassNotFoundException var1) {
         throw new AssertionError(var1);
      } catch (NoSuchMethodException var2) {
         throw new AssertionError(var2);
      }
   }

   private static class SdpSocket extends Socket {
      SdpSocket(SocketImpl var1) throws SocketException {
         super(var1);
      }
   }
}
