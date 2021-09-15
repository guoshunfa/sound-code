package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Enumeration;

public interface InetAddressAcl {
   String getName();

   boolean checkReadPermission(InetAddress var1);

   boolean checkReadPermission(InetAddress var1, String var2);

   boolean checkCommunity(String var1);

   boolean checkWritePermission(InetAddress var1);

   boolean checkWritePermission(InetAddress var1, String var2);

   Enumeration<InetAddress> getTrapDestinations();

   Enumeration<String> getTrapCommunities(InetAddress var1);

   Enumeration<InetAddress> getInformDestinations();

   Enumeration<String> getInformCommunities(InetAddress var1);
}
