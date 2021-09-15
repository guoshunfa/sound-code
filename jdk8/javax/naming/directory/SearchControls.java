package javax.naming.directory;

import java.io.Serializable;

public class SearchControls implements Serializable {
   public static final int OBJECT_SCOPE = 0;
   public static final int ONELEVEL_SCOPE = 1;
   public static final int SUBTREE_SCOPE = 2;
   private int searchScope;
   private int timeLimit;
   private boolean derefLink;
   private boolean returnObj;
   private long countLimit;
   private String[] attributesToReturn;
   private static final long serialVersionUID = -2480540967773454797L;

   public SearchControls() {
      this.searchScope = 1;
      this.timeLimit = 0;
      this.countLimit = 0L;
      this.derefLink = false;
      this.returnObj = false;
      this.attributesToReturn = null;
   }

   public SearchControls(int var1, long var2, int var4, String[] var5, boolean var6, boolean var7) {
      this.searchScope = var1;
      this.timeLimit = var4;
      this.derefLink = var7;
      this.returnObj = var6;
      this.countLimit = var2;
      this.attributesToReturn = var5;
   }

   public int getSearchScope() {
      return this.searchScope;
   }

   public int getTimeLimit() {
      return this.timeLimit;
   }

   public boolean getDerefLinkFlag() {
      return this.derefLink;
   }

   public boolean getReturningObjFlag() {
      return this.returnObj;
   }

   public long getCountLimit() {
      return this.countLimit;
   }

   public String[] getReturningAttributes() {
      return this.attributesToReturn;
   }

   public void setSearchScope(int var1) {
      this.searchScope = var1;
   }

   public void setTimeLimit(int var1) {
      this.timeLimit = var1;
   }

   public void setDerefLinkFlag(boolean var1) {
      this.derefLink = var1;
   }

   public void setReturningObjFlag(boolean var1) {
      this.returnObj = var1;
   }

   public void setCountLimit(long var1) {
      this.countLimit = var1;
   }

   public void setReturningAttributes(String[] var1) {
      this.attributesToReturn = var1;
   }
}
