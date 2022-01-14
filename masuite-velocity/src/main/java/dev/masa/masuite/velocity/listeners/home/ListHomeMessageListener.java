package dev.masa.masuite.velocity.listeners.home;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import dev.masa.masuite.api.proxy.listeners.home.IListHomeMessageListener;
import dev.masa.masuite.common.models.Home;
import dev.masa.masuite.common.models.User;
import dev.masa.masuite.common.objects.MaSuiteMessage;
import dev.masa.masuite.velocity.MaSuiteVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static dev.masa.masuite.velocity.MaSuiteVelocity.MASUITE_MAIN_CHANNEL;

public record ListHomeMessageListener(MaSuiteVelocity plugin) implements IListHomeMessageListener<PluginMessageEvent> {

    @Subscribe
    public void listHomes(PluginMessageEvent event) throws IOException {
        if (!event.getIdentifier().equals(MASUITE_MAIN_CHANNEL)) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_LIST.channel)) {
            return;
        }

        Player player = (Player) event.getTarget();

        List<Home> homes = this.plugin.homeService().homes(player.getUniqueId());

        this.listHomes(player, homes, this.plugin.messages().homes().homeListName(), player.getUsername());
    }

    @Subscribe
    public void listUserHomes(PluginMessageEvent event) throws IOException {
        if (!event.getIdentifier().equals(MASUITE_MAIN_CHANNEL)) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        String channel = in.readUTF();
        if (!channel.equals(MaSuiteMessage.HOMES_LIST_OTHERS.channel)) {
            return;
        }

        Player player = (Player) event.getTarget();

        String username = in.readUTF();
        Optional<User> user = this.plugin.userService().user(username);

        if (user.isEmpty()) {
            player.sendMessage(this.plugin.messages().playerNotFound());
            return;
        }

        // Query homes and send them to player
        List<Home> homes = this.plugin.homeService().homes(user.get().uniqueId());

        this.listHomes(player, homes, this.plugin.messages().homes().homeListTitleOthers(), user.get().username());
    }

    private void listHomes(Player player, List<Home> homes, Component title, String ownerName) {
        Component message = this.plugin.messages().homes().homeListTitle();

        for (Home home : homes) {
            TextReplacementConfig replacement = TextReplacementConfig.builder().match("%player%").replacement(ownerName).match("%home%").replacement(home.name()).build();
            message = message.append(title.replaceText(replacement)).append(this.plugin.messages().homes().homeListSplitter());
        }

        player.sendMessage(message);
    }
}