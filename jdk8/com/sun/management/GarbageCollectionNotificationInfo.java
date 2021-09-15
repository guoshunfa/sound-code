package com.sun.management;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import jdk.Exported;
import sun.management.GarbageCollectionNotifInfoCompositeData;

@Exported
public class GarbageCollectionNotificationInfo implements CompositeDataView {
   private final String gcName;
   private final String gcAction;
   private final String gcCause;
   private final GcInfo gcInfo;
   private final CompositeData cdata;
   public static final String GARBAGE_COLLECTION_NOTIFICATION = "com.sun.management.gc.notification";

   public GarbageCollectionNotificationInfo(String var1, String var2, String var3, GcInfo var4) {
      if (var1 == null) {
         throw new NullPointerException("Null gcName");
      } else if (var2 == null) {
         throw new NullPointerException("Null gcAction");
      } else if (var3 == null) {
         throw new NullPointerException("Null gcCause");
      } else {
         this.gcName = var1;
         this.gcAction = var2;
         this.gcCause = var3;
         this.gcInfo = var4;
         this.cdata = new GarbageCollectionNotifInfoCompositeData(this);
      }
   }

   GarbageCollectionNotificationInfo(CompositeData var1) {
      GarbageCollectionNotifInfoCompositeData.validateCompositeData(var1);
      this.gcName = GarbageCollectionNotifInfoCompositeData.getGcName(var1);
      this.gcAction = GarbageCollectionNotifInfoCompositeData.getGcAction(var1);
      this.gcCause = GarbageCollectionNotifInfoCompositeData.getGcCause(var1);
      this.gcInfo = GarbageCollectionNotifInfoCompositeData.getGcInfo(var1);
      this.cdata = var1;
   }

   public String getGcName() {
      return this.gcName;
   }

   public String getGcAction() {
      return this.gcAction;
   }

   public String getGcCause() {
      return this.gcCause;
   }

   public GcInfo getGcInfo() {
      return this.gcInfo;
   }

   public static GarbageCollectionNotificationInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof GarbageCollectionNotifInfoCompositeData ? ((GarbageCollectionNotifInfoCompositeData)var0).getGarbageCollectionNotifInfo() : new GarbageCollectionNotificationInfo(var0);
      }
   }

   public CompositeData toCompositeData(CompositeType var1) {
      return this.cdata;
   }
}
