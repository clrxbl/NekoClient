package dev.neko.nekoclient.structure;

public interface ThrowingRunnable<T extends Throwable> {
   void run() throws T;
}
