package java.awt.image;

public interface ImageProducer {
   void addConsumer(ImageConsumer var1);

   boolean isConsumer(ImageConsumer var1);

   void removeConsumer(ImageConsumer var1);

   void startProduction(ImageConsumer var1);

   void requestTopDownLeftRightResend(ImageConsumer var1);
}
