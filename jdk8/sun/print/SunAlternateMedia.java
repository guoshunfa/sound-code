package sun.print;

import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.Media;

public class SunAlternateMedia implements PrintRequestAttribute {
   private static final long serialVersionUID = -8878868345472850201L;
   private Media media;

   public SunAlternateMedia(Media var1) {
      this.media = var1;
   }

   public Media getMedia() {
      return this.media;
   }

   public final Class getCategory() {
      return SunAlternateMedia.class;
   }

   public final String getName() {
      return "sun-alternate-media";
   }

   public String toString() {
      return "alternate-media: " + this.media.toString();
   }

   public int hashCode() {
      return this.media.hashCode();
   }
}
