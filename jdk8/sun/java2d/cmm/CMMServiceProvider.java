package sun.java2d.cmm;

public abstract class CMMServiceProvider {
   public final PCMM getColorManagementModule() {
      return CMSManager.canCreateModule() ? this.getModule() : null;
   }

   protected abstract PCMM getModule();
}
