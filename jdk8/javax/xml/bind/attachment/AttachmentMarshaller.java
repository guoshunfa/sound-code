package javax.xml.bind.attachment;

import javax.activation.DataHandler;

public abstract class AttachmentMarshaller {
   public abstract String addMtomAttachment(DataHandler var1, String var2, String var3);

   public abstract String addMtomAttachment(byte[] var1, int var2, int var3, String var4, String var5, String var6);

   public boolean isXOPPackage() {
      return false;
   }

   public abstract String addSwaRefAttachment(DataHandler var1);
}
