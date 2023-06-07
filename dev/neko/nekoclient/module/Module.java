package dev.neko.nekoclient.module;

import dev.neko.nekoclient.Client;
import dev.neko.nekoclient.structure.ThrowingRunnable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public abstract class Module {
   protected final Client client;
   private boolean enabled;
   private ScheduledFuture<?> future;
   private final ScheduledExecutorService service;

   public Module(Client client) {
      this.client = client;
      this.enabled = false;
      this.service = Executors.newSingleThreadScheduledExecutor();
   }

   public void setEnabled(boolean enabled) throws IOException {
      if (this.enabled == enabled) {
         throw new IllegalStateException(String.format("Module already %s", enabled ? "enabled" : "disabled"));
      } else {
         this.enabled = enabled;
         if (this.enabled) {
            Module.StartAction action = this.run(this.service);
            if (Objects.nonNull(action.getRunnable())) {
               action.getRunnable().run();
            }

            if (Objects.nonNull(action.getFuture())) {
               this.future = action.getFuture();
            }
         } else if (Objects.nonNull(this.future) && !this.future.isCancelled() && !this.future.isDone()) {
            this.future.cancel(true);
            this.future = null;
         }
      }
   }

   public abstract String getName();

   protected abstract Module.StartAction run(ScheduledExecutorService var1);

   public final boolean isEnabled() {
      return this.enabled;
   }

   protected final Client getClient() {
      return this.client;
   }

   public class StartAction {
      private ThrowingRunnable<IOException> runnable;
      private ScheduledFuture<?> future;

      public Module.StartAction run(ThrowingRunnable<IOException> runnable) {
         this.runnable = runnable;
         return this;
      }

      public Module.StartAction schedule(ScheduledFuture<?> future) {
         this.future = future;
         return this;
      }

      public final ThrowingRunnable<IOException> getRunnable() {
         return this.runnable;
      }

      public final ScheduledFuture<?> getFuture() {
         return this.future;
      }
   }
}
