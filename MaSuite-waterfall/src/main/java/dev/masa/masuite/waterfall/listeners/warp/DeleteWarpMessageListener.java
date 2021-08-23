package dev.masa.masuite.waterfall.listeners.warp;

import dev.masa.masuite.common.models.Warp;
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

public class DeleteWarpMessageListener implements Listener {

    private final MaSuiteWaterfall plugin;

    public DeleteWarpMessageListener(MaSuiteWaterfall plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void deleteWarp(PluginMessageEvent event) throws IOException {
        if (!event.getTag().equals(MaSuiteMessage.MAIN.channel)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_DELETE.channel)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        String name = in.readUTF();

        Optional<Warp> warp = this.plugin.warpService().warp(name);

        Audience audience = this.plugin.adventure().player(player);
        if (warp.isEmpty()) {
            audience.sendMessage(this.plugin.messages().warps().warpNotFound());
            return;
        }

        TextReplacementConfig replacement = TextReplacementConfig.builder()
                .match("%warp%")
                .replacement(warp.get().name())
                .build();

        this.plugin.warpService().deleteWarp(warp.get(), done -> {
            if (done) {
                audience.sendMessage(this.plugin.messages().warps().warpDeleted().replaceText(replacement));
            } else {
                audience.sendMessage(Component.text("An error occurred while deleting home", NamedTextColor.RED));
            }
        });
    }


}
