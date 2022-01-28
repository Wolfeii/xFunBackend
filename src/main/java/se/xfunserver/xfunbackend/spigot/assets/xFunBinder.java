package se.xfunserver.xfunbackend.spigot.assets;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import se.xfunserver.xfunbackend.spigot.Core;

public class xFunBinder extends AbstractModule {

    private final Core plugin;

    public xFunBinder(Core plugin) {
        this.plugin = plugin;
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(Core.class).toInstance(this.plugin);
    }
}
