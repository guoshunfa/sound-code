package java.net;

import java.io.IOException;

class SocketSecrets {
   private static <T> void setOption(Object var0, SocketOption<T> var1, T var2) throws IOException {
      SocketImpl var3;
      if (var0 instanceof Socket) {
         var3 = ((Socket)var0).getImpl();
      } else {
         if (!(var0 instanceof ServerSocket)) {
            throw new IllegalArgumentException();
         }

         var3 = ((ServerSocket)var0).getImpl();
      }

      var3.setOption(var1, var2);
   }

   private static <T> T getOption(Object var0, SocketOption<T> var1) throws IOException {
      SocketImpl var2;
      if (var0 instanceof Socket) {
         var2 = ((Socket)var0).getImpl();
      } else {
         if (!(var0 instanceof ServerSocket)) {
            throw new IllegalArgumentException();
         }

         var2 = ((ServerSocket)var0).getImpl();
      }

      return var2.getOption(var1);
   }

   private static <T> void setOption(DatagramSocket var0, SocketOption<T> var1, T var2) throws IOException {
      var0.getImpl().setOption(var1, var2);
   }

   private static <T> T getOption(DatagramSocket var0, SocketOption<T> var1) throws IOException {
      return var0.getImpl().getOption(var1);
   }
}
