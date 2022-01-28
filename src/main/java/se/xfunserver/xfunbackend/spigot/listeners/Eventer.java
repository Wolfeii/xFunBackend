package se.xfunserver.xfunbackend.spigot.listeners;

public interface Eventer<T> {
    public void run(T e);
}
