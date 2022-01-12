package dev.masa.masuite.waterfall.listeners.home;

import dev.masa.masuite.common.models.Home;
import dev.masa.masuite.common.models.User;
import dev.masa.masuite.common.objects.MaSuiteMessage;
import dev.masa.masuite.waterfall.MaSuiteWaterfall;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;

public class DeleteHomeMessageListener implements Listener {

    private final MaSuiteWaterfall plugin;

    public DeleteHomeMessageListener(MaSuiteWaterfall plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void deleteHome(PluginMessageEvent event) throws IOException {
        if(!event.getTag().equals(MaSuiteMessage.MAIN.channel)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_DELETE.channel)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        String name = in.readUTF();

        Optional<Home> home = this.plugin.homeService().home(player.getUniqueId(), name);

        this.delete(player, home);
    }

    @EventHandler
    public void deleteHomeOthers(PluginMessageEvent event) throws IOException {
        if(!event.getTag().equals(MaSuiteMessage.MAIN.channel)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_DELETE_OTHERS.channel)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        Audience audience = this.plugin.adventure().player(player);

        // Get targeted user
        String username = in.readUTF();
        Optional<User> user = this.plugin.userService().user(username);

        if (user.isEmpty()) {
            audience.sendMessage(this.plugin.messages().playerNotFound());
            return;
        }

        // Find & delete
        String name = in.readUTF();

        Optional<Home> home = this.plugin.homeService().home(user.get().uniqueId(), name);

        this.delete(player, home);

    }



    private void delete(ProxiedPlayer player, Optional<Home> home) {
        Audience audience = this.plugin.adventure().player(player);
        if(home.isEmpty()) {
            audience.sendMessage(this.plugin.messages().homes().homeNotFound());
            return;
        }

        TextReplacementConfig replacement = TextReplacementConfig.builder()
                .match("%home%")
                .replacement(home.get().name())
                .build();

        this.plugin.homeService().deleteHome(home.get(), done -> {
            if(done) {
                audience.sendMessage(this.plugin.messages().homes().homeDeleted().replaceText(replacement));
            } else {
                audience.sendMessage(Component.text("An error occurred while deleting home", NamedTextColor.RED));
            }
        });
    }
}