package java.net;

interface SocksConsts {
   int PROTO_VERS4 = 4;
   int PROTO_VERS = 5;
   int DEFAULT_PORT = 1080;
   int NO_AUTH = 0;
   int GSSAPI = 1;
   int USER_PASSW = 2;
   int NO_METHODS = -1;
   int CONNECT = 1;
   int BIND = 2;
   int UDP_ASSOC = 3;
   int IPV4 = 1;
   int DOMAIN_NAME = 3;
   int IPV6 = 4;
   int REQUEST_OK = 0;
   int GENERAL_FAILURE = 1;
   int NOT_ALLOWED = 2;
   int NET_UNREACHABLE = 3;
   int HOST_UNREACHABLE = 4;
   int CONN_REFUSED = 5;
   int TTL_EXPIRED = 6;
   int CMD_NOT_SUPPORTED = 7;
   int ADDR_TYPE_NOT_SUP = 8;
}
