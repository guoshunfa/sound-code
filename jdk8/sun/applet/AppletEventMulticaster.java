package sun.applet;

public class AppletEventMulticaster implements AppletListener {
   private final AppletListener a;
   private final AppletListener b;

   public AppletEventMulticaster(AppletListener var1, AppletListener var2) {
      this.a = var1;
      this.b = var2;
   }

   public void appletStateChanged(AppletEvent var1) {
      this.a.appletStateChanged(var1);
      this.b.appletStateChanged(var1);
   }

   public static AppletListener add(AppletListener var0, AppletListener var1) {
      return addInternal(var0, var1);
   }

   public static AppletListener remove(AppletListener var0, AppletListener var1) {
      return removeInternal(var0, var1);
   }

   private static AppletListener addInternal(AppletListener var0, AppletListener var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (AppletListener)(var1 == null ? var0 : new AppletEventMulticaster(var0, var1));
      }
   }

   protected AppletListener remove(AppletListener var1) {
      if (var1 == this.a) {
         return this.b;
      } else if (var1 == this.b) {
         return this.a;
      } else {
         AppletListener var2 = removeInternal(this.a, var1);
         AppletListener var3 = removeInternal(this.b, var1);
         return (AppletListener)(var2 == this.a && var3 == this.b ? this : addInternal(var2, var3));
      }
   }

   private static AppletListener removeInternal(AppletListener var0, AppletListener var1) {
      if (var0 != var1 && var0 != null) {
         return var0 instanceof AppletEventMulticaster ? ((AppletEventMulticaster)var0).remove(var1) : var0;
      } else {
         return null;
      }
   }
}
