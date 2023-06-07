package dev.neko.nekoclient.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.StringJoiner;

public class FormUtil {
   public static String encodeToForm(Map<String, String> map) {
      StringJoiner joiner = new StringJoiner("&");
      map.forEach((key, value) -> {
         try {
            joiner.add(String.format("%s=%s", key, URLEncoder.encode(value, "UTF-8")));
         } catch (UnsupportedEncodingException var4) {
         }
      });
      return joiner.toString();
   }
}
