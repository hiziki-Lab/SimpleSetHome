package xyz.hiziki;

import java.io.File;
import java.io.IOException;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Main extends JavaPlugin
{
    private final File homesFile = new File(getDataFolder(), "Homes.yml");
    private final YamlConfiguration homes = YamlConfiguration.loadConfiguration(homesFile);

    @Override
    public void onEnable() //プラグインが起動した時
    {
        super.onEnable();

        getLogger().info("プラグインは正常に起動しました。");
    }

    @Override
    public void onDisable() //プラグインが停止した時
    {
        super.onDisable();

        saveFile();

        getLogger().info("プラグインは正常に停止しました。");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args)
    {
        if(command.getName().equalsIgnoreCase("sethome"))
        {
            if (sender instanceof ConsoleCommandSender)
            {
                getLogger().info("コマンドを実行出来るのはプレイヤーのみです。");
                return false;
            }
            else
            {
                Player p = (Player) sender;
                homes.set("Homes." + p.getUniqueId() + ".World", p.getWorld().getName());
                homes.set("Homes." + p.getUniqueId() + ".X", p.getLocation().getX());
                homes.set("Homes." + p.getUniqueId() + ".Y", p.getLocation().getY());
                homes.set("Homes." + p.getUniqueId() + ".Z", p.getLocation().getZ());
                saveFile();
                p.sendMessage("ホームを設定しました。");
            }
            return false;
        }
        if(command.getName().equalsIgnoreCase("home"))
        {
            if (sender instanceof ConsoleCommandSender)
            {
                getLogger().info("コマンドを実行出来るのはプレイヤーのみです。");
                return false;
            }
            else
            {
                Player p = (Player) sender;
                if (homes.getString("Homes." + p.getUniqueId()) == null)
                {
                    sender.sendMessage("ホームが設定されていません");
                }
                else
                {
                    World world = Bukkit.getWorld(homes.getString("Homes." + p.getUniqueId() + ".World"));
                    double x = homes.getDouble("Homes." + p.getUniqueId() + ".X");
                    double y = homes.getDouble("Homes." + p.getUniqueId() + ".Y");
                    double z = homes.getDouble("Homes." + p.getUniqueId() + ".Z");
                    p.teleport(new Location(world, x, y, z));
                }
            }
        }
        return false;
    }

    private void saveFile()
    {
        try
        {
            homes.save(homesFile);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
