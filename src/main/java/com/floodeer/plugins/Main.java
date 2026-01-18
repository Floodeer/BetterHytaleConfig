package com.floodeer.plugins;

import com.floodeer.plugins.configuration.ConfigKey;
import com.floodeer.plugins.configuration.PluginConfiguration;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private final Config<ConfigExample> config;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("Settings", new PluginConfiguration<>(ConfigExample.class, ConfigExample::new).codec());
    }

    @Override
    protected void setup() {
        this.config.save();

        ConfigExample plConfig = config.get();

        getLogger().at(Level.INFO).log("String: " + plConfig.testString);
        getLogger().at(Level.INFO).log("Integer: " + plConfig.testInteger);
        getLogger().at(Level.INFO).log("Boolean: " + plConfig.testBoolean);

        getLogger().at(Level.INFO).log("Array test: ");
        for(String s : plConfig.arrayTest) {
            getLogger().at(Level.INFO).log(s);
        }
    }

    @Override
    protected void shutdown() {

    }

    private static class ConfigExample {
        @ConfigKey("String-Test")
        public String testString = "Testing";

        @ConfigKey("Integer-Test")
        public int testInteger = 10;

        @ConfigKey("Boolean-Test")
        public boolean testBoolean = true;

        @ConfigKey("Array-Test")
        public String[] arrayTest = { "A", "B", "C", "D"};

    }
}
