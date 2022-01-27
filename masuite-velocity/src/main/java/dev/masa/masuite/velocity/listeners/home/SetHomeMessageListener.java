package dev.masa.masuite.velocity.listeners.home;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import dev.masa.masuite.api.proxy.listeners.home.ISetHomeMessageListener;
import dev.masa.masuite.common.models.home.Home;
import dev.masa.masuite.common.objects.Location;
import dev.masa.masuite.common.objects.MaSuiteMessage;
import dev.masa.masuite.common.services.MessageService;
import dev.masa.masuite.velocity.MaSuiteVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static dev.masa.masuite.velocity.MaSuiteVelocity.MASUITE_MAIN_CHANNEL;

public record SetHomeMessageListener(MaSuiteVelocity plugin) implements ISetHomeMessageListener<PluginMessageEvent> {

    @Subscribe
    public void createHome(PluginMessageEvent event) throws IOException {
        if (!event.getIdentifier().equals(MASUITE_MAIN_CHANNEL)) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_SET.channel)) {
            return;
        }
        Player player = (Player) event.getTarget();
        String name = in.readUTF();
        Location loc = new Location().deserialize(in.readUTF());
        loc.server(player.getCurrentServer().get().getServerInfo().getName());

        Home home = new Home(name, player.getUniqueId(), loc);
        this.createHome(player, home);
    }

    @Subscribe
    public void createHomeOthers(PluginMessageEvent event) throws IOException {
        if (!event.getIdentifier().equals(MASUITE_MAIN_CHANNEL)) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_SET_OTHERS.channel)) {
            return;
        }
        Player player = (Player) event.getTarget();

        String username = in.readUTF();
        String name = in.readUTF();
        Location loc = new Location().deserialize(in.readUTF());
        loc.server(player.getCurrentServer().get().getServerInfo().getName());

        this.plugin.userService().user(username).thenAcceptAsync((user) -> {
            if (user.isEmpty()) {
                MessageService.sendMessage(player, this.plugin.messages().playerNotFound());
                return;
            }

            Home home = new Home(name, user.get().uniqueId(), loc);
            this.createHome(player, home);
        });


    }

    private void createHome(Player player, Home home) {
        this.plugin.homeService().createOrUpdateHome(home, (done, isCreated) -> {
            if (!done) {
                player.sendMessage(Component.text("An error occurred while creating / updating home.", NamedTextColor.RED));
                return;
            }
            if (isCreated) {
                MessageService.sendMessage(player, this.plugin.messages().homes().homeSet(), MessageService.Templates.homeTemplate(home));
            } else {
                MessageService.sendMessage(player, this.plugin.messages().homes().homeUpdated(), MessageService.Templates.homeTemplate(home));
            }
        });
    }
}
