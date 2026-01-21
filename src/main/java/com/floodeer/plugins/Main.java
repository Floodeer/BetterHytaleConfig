package com.floodeer.plugins;

import com.floodeer.plugins.configuration.ConfigKey;
import com.floodeer.plugins.configuration.PluginConfiguration;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.util.Config;

import javax.annotation.Nonnull;

public class Main extends JavaPlugin {

    private final Config<ConfigExample> config;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("Settings", new PluginConfiguration<>(ConfigExample.class, ConfigExample::new).codec());
    }

    @Override
    protected void setup() {
        this.config.save();

        getCommandRegistry().registerCommand(new CommandBase("reloadconfig", "Reloads the config") {
            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                config.load();

                context.sendMessage(Message.raw("String: " + config.get().testString));
                context.sendMessage(Message.raw("Integer: " + config.get().testInteger));
                context.sendMessage(Message.raw("Boolean: " + config.get().testBoolean));
                context.sendMessage(Message.raw("Double value: " + config.get().doubleTest));

                context.sendMessage(Message.raw("Array test:"));
                for (String s : config.get().arrayTest) {
                    context.sendMessage(Message.raw(s));
                }
            }
        });
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

        @ConfigKey("Double-Test")
        public double doubleTest = 3.14;
    }


    @Override
    protected void shutdown() {

    }
}
