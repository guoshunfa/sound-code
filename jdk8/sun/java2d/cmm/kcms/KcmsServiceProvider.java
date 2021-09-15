package sun.java2d.cmm.kcms;

import sun.java2d.cmm.CMMServiceProvider;
import sun.java2d.cmm.PCMM;

public final class KcmsServiceProvider extends CMMServiceProvider {
   protected PCMM getModule() {
      return CMM.getModule();
   }
}
