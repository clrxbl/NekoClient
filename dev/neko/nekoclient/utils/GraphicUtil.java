package dev.neko.nekoclient.utils;

import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GraphicUtil {
   public static void drawCenteredText(Window window, String text) {
      window.getGraphics()
         .drawString(
            text, window.getWidth() / 2 - window.getGraphics().getFontMetrics(window.getGraphics().getFont()).stringWidth(text) / 2, window.getHeight() / 2
         );
   }

   public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
      BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, 1);
      Graphics2D graphics2D = resizedImage.createGraphics();
      graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
      graphics2D.dispose();
      return resizedImage;
   }
}
