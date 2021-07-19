package dev.masa.masuite.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.masa.masuite.common.objects.MaSuiteMessage;
import dev.masa.masuite.paper.MaSuitePaper;
import dev.masa.masuite.paper.utils.BukkitPluginMessage;
import org.bukkit.entity.Player;

@CommandAlias("home|teleporthome")
public class TeleportHomeCommand extends BaseCommand {

    private final MaSuitePaper plugin;

    public TeleportHomeCommand(MaSuitePaper plugin) {
        this.plugin = plugin;
    }

    @Default()
    @CommandPermission("masuite.home.teleport")
    @Description("Teleport to home")
    public void setHome(Player player, @Single @Default("home") String home) {
        BukkitPluginMessage bpm = new BukkitPluginMessage(player, MaSuiteMessage.HOMES_TELEPORT, home);
        bpm.send();
    }

}
