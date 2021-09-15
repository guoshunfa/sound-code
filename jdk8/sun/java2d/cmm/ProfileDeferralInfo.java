package sun.java2d.cmm;

import java.io.IOException;
import java.io.InputStream;

public class ProfileDeferralInfo extends InputStream {
   public int colorSpaceType;
   public int numComponents;
   public int profileClass;
   public String filename;

   public ProfileDeferralInfo(String var1, int var2, int var3, int var4) {
      this.filename = var1;
      this.colorSpaceType = var2;
      this.numComponents = var3;
      this.profileClass = var4;
   }

   public int read() throws IOException {
      return 0;
   }
}
