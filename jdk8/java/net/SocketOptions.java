package java.net;

public interface SocketOptions {
   int TCP_NODELAY = 1;
   int SO_BINDADDR = 15;
   int SO_REUSEADDR = 4;
   int SO_BROADCAST = 32;
   int IP_MULTICAST_IF = 16;
   int IP_MULTICAST_IF2 = 31;
   int IP_MULTICAST_LOOP = 18;
   int IP_TOS = 3;
   int SO_LINGER = 128;
   int SO_TIMEOUT = 4102;
   int SO_SNDBUF = 4097;
   int SO_RCVBUF = 4098;
   int SO_KEEPALIVE = 8;
   int SO_OOBINLINE = 4099;

   void setOption(int var1, Object var2) throws SocketException;

   Object getOption(int var1) throws SocketException;
}
