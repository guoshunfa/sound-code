package sun.management.jdp;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class JdpJmxPacket extends JdpGenericPacket implements JdpPacket {
   public static final String UUID_KEY = "DISCOVERABLE_SESSION_UUID";
   public static final String MAIN_CLASS_KEY = "MAIN_CLASS";
   public static final String JMX_SERVICE_URL_KEY = "JMX_SERVICE_URL";
   public static final String INSTANCE_NAME_KEY = "INSTANCE_NAME";
   public static final String PROCESS_ID_KEY = "PROCESS_ID";
   public static final String RMI_HOSTNAME_KEY = "RMI_HOSTNAME";
   public static final String BROADCAST_INTERVAL_KEY = "BROADCAST_INTERVAL";
   private UUID id;
   private String mainClass;
   private String jmxServiceUrl;
   private String instanceName;
   private String processId;
   private String rmiHostname;
   private String broadcastInterval;

   public JdpJmxPacket(UUID var1, String var2) {
      this.id = var1;
      this.jmxServiceUrl = var2;
   }

   public JdpJmxPacket(byte[] var1) throws JdpException {
      JdpPacketReader var2 = new JdpPacketReader(var1);
      Map var3 = var2.getDiscoveryDataAsMap();
      String var4 = (String)var3.get("DISCOVERABLE_SESSION_UUID");
      this.id = var4 == null ? null : UUID.fromString(var4);
      this.jmxServiceUrl = (String)var3.get("JMX_SERVICE_URL");
      this.mainClass = (String)var3.get("MAIN_CLASS");
      this.instanceName = (String)var3.get("INSTANCE_NAME");
      this.processId = (String)var3.get("PROCESS_ID");
      this.rmiHostname = (String)var3.get("RMI_HOSTNAME");
      this.broadcastInterval = (String)var3.get("BROADCAST_INTERVAL");
   }

   public void setMainClass(String var1) {
      this.mainClass = var1;
   }

   public void setInstanceName(String var1) {
      this.instanceName = var1;
   }

   public UUID getId() {
      return this.id;
   }

   public String getMainClass() {
      return this.mainClass;
   }

   public String getJmxServiceUrl() {
      return this.jmxServiceUrl;
   }

   public String getInstanceName() {
      return this.instanceName;
   }

   public String getProcessId() {
      return this.processId;
   }

   public void setProcessId(String var1) {
      this.processId = var1;
   }

   public String getRmiHostname() {
      return this.rmiHostname;
   }

   public void setRmiHostname(String var1) {
      this.rmiHostname = var1;
   }

   public String getBroadcastInterval() {
      return this.broadcastInterval;
   }

   public void setBroadcastInterval(String var1) {
      this.broadcastInterval = var1;
   }

   public byte[] getPacketData() throws IOException {
      JdpPacketWriter var1 = new JdpPacketWriter();
      var1.addEntry("DISCOVERABLE_SESSION_UUID", this.id == null ? null : this.id.toString());
      var1.addEntry("MAIN_CLASS", this.mainClass);
      var1.addEntry("JMX_SERVICE_URL", this.jmxServiceUrl);
      var1.addEntry("INSTANCE_NAME", this.instanceName);
      var1.addEntry("PROCESS_ID", this.processId);
      var1.addEntry("RMI_HOSTNAME", this.rmiHostname);
      var1.addEntry("BROADCAST_INTERVAL", this.broadcastInterval);
      return var1.getPacketBytes();
   }

   public int hashCode() {
      byte var1 = 1;
      int var2 = var1 * 31 + this.id.hashCode();
      var2 = var2 * 31 + this.jmxServiceUrl.hashCode();
      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof JdpJmxPacket) {
         JdpJmxPacket var2 = (JdpJmxPacket)var1;
         return Objects.equals(this.id, var2.getId()) && Objects.equals(this.jmxServiceUrl, var2.getJmxServiceUrl());
      } else {
         return false;
      }
   }
}
