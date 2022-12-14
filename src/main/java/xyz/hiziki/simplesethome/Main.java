package xyz.hiziki.simplesethome;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.hiziki.simplesethome.command.CommandManager;
import xyz.hiziki.simplesethome.config.ConfigManager;

import java.io.File;

/*
*
* Main
* 作成：hi_kun_JPN
* 注意点
* 変数のカプセル化をしています。
*
* */

public final class Main extends JavaPlugin
{
    private static JavaPlugin plugin;

    private static File homesFile;

    private static YamlConfiguration homes;

    @Override
    public void onEnable()
    {
        //プラグイン起動時の処理

        super.onEnable(); //絶対書け

        plugin = this;

        homesFile = new File(getDataFolder(), "home.yml"); //ファイル作成
        homes = YamlConfiguration.loadConfiguration(homesFile);

        new ConfigManager(this);
        new CommandManager(this);

        getLogger().info("plugin has been successfully startup."); //プラグイン起動時にログを表示
    }

    @Override
    public void onDisable()
    {
        //プラグイン停止時の処理

        super.onDisable(); //絶対書け

        getLogger().info("plugin has been successfully shutdown."); //プラグイン停止時にログを表示
    }

    public static JavaPlugin getPlugin() //カプセル化
    {
        return plugin; //pluginを返す
    }

    public static File getHomesFile() //カプセル化
    {
        return homesFile; //homesFileを返す
    }

    public static YamlConfiguration getHomes() //カプセル化
    {
        return homes; //homesを返す
    }
}
