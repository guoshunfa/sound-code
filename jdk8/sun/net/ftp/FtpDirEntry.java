package sun.net.ftp;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class FtpDirEntry {
   private final String name;
   private String user = null;
   private String group = null;
   private long size = -1L;
   private Date created = null;
   private Date lastModified = null;
   private FtpDirEntry.Type type;
   private boolean[][] permissions;
   private HashMap<String, String> facts;

   private FtpDirEntry() {
      this.type = FtpDirEntry.Type.FILE;
      this.permissions = (boolean[][])null;
      this.facts = new HashMap();
      this.name = null;
   }

   public FtpDirEntry(String var1) {
      this.type = FtpDirEntry.Type.FILE;
      this.permissions = (boolean[][])null;
      this.facts = new HashMap();
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public String getUser() {
      return this.user;
   }

   public FtpDirEntry setUser(String var1) {
      this.user = var1;
      return this;
   }

   public String getGroup() {
      return this.group;
   }

   public FtpDirEntry setGroup(String var1) {
      this.group = var1;
      return this;
   }

   public long getSize() {
      return this.size;
   }

   public FtpDirEntry setSize(long var1) {
      this.size = var1;
      return this;
   }

   public FtpDirEntry.Type getType() {
      return this.type;
   }

   public FtpDirEntry setType(FtpDirEntry.Type var1) {
      this.type = var1;
      return this;
   }

   public Date getLastModified() {
      return this.lastModified;
   }

   public FtpDirEntry setLastModified(Date var1) {
      this.lastModified = var1;
      return this;
   }

   public boolean canRead(FtpDirEntry.Permission var1) {
      return this.permissions != null ? this.permissions[var1.value][0] : false;
   }

   public boolean canWrite(FtpDirEntry.Permission var1) {
      return this.permissions != null ? this.permissions[var1.value][1] : false;
   }

   public boolean canExexcute(FtpDirEntry.Permission var1) {
      return this.permissions != null ? this.permissions[var1.value][2] : false;
   }

   public FtpDirEntry setPermissions(boolean[][] var1) {
      this.permissions = var1;
      return this;
   }

   public FtpDirEntry addFact(String var1, String var2) {
      this.facts.put(var1.toLowerCase(), var2);
      return this;
   }

   public String getFact(String var1) {
      return (String)this.facts.get(var1.toLowerCase());
   }

   public Date getCreated() {
      return this.created;
   }

   public FtpDirEntry setCreated(Date var1) {
      this.created = var1;
      return this;
   }

   public String toString() {
      return this.lastModified == null ? this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") " + this.size : this.name + " [" + this.type + "] (" + this.user + " / " + this.group + ") {" + this.size + "} " + DateFormat.getDateInstance().format(this.lastModified);
   }

   public static enum Permission {
      USER(0),
      GROUP(1),
      OTHERS(2);

      int value;

      private Permission(int var3) {
         this.value = var3;
      }
   }

   public static enum Type {
      FILE,
      DIR,
      PDIR,
      CDIR,
      LINK;
   }
}
