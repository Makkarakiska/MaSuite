package dev.masa.masuite.waterfall.listeners.warp;

import dev.masa.masuite.api.proxy.listeners.warp.ISetWarpMessageListener;
import dev.masa.masuite.common.models.warp.Warp;
import dev.masa.masuite.common.objects.Location;
import dev.masa.masuite.common.objects.MaSuiteMessage;
import dev.masa.masuite.common.services.MessageService;
import dev.masa.masuite.waterfall.MaSuiteWaterfall;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public record SetWarpMessageListener(
        MaSuiteWaterfall plugin) implements Listener, ISetWarpMessageListener<PluginMessageEvent> {

    @EventHandler
    public void createWarp(PluginMessageEvent event) throws IOException {
        if (!event.getTag().equals(MaSuiteMessage.MAIN.channel)) {
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.WARPS_SET.channel)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        String name = in.readUTF();
        String location = in.readUTF();
        boolean isPublic = in.readBoolean();
        boolean global = in.readBoolean();

        // Deserialize location and assign correct server
        Location loc = new Location().deserialize(location);
        loc.server(player.getServer().getInfo().getName());

        Warp warp = new Warp(name, loc, isPublic, global);

        Audience audience = this.plugin.adventure().player(player);

        this.plugin.warpService().createOrUpdateWarp(warp, (done, isCreated) -> {
            if (!done) {
                audience.sendMessage(Component.text("An error occurred while creating / updating warp.", NamedTextColor.RED));
                return;
            }
            if (isCreated) {
                MessageService.sendMessage(audience, this.plugin.messages().warps().warpCreated(), MessageService.Templates.warpTemplate(warp));
            } else {
                MessageService.sendMessage(audience, this.plugin.messages().warps().warpUpdated(), MessageService.Templates.warpTemplate(warp));
            }
        });
    }
}
