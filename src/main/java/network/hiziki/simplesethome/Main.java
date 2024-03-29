package network.hiziki.simplesethome;

import network.hiziki.simplesethome.command.CommandManager;
import network.hiziki.simplesethome.config.ConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/*
*
* Main
* 作成：hi_kun_JPN
* 注意点
* 変数のカプセル化をしています。
*
* */

public final class Main extends JavaPlugin {
    private static JavaPlugin plugin;
    private static File homesFile;
    private static YamlConfiguration homes;

    @Override //プラグイン起動時の処理
    public void onEnable() {
        super.onEnable(); //絶対書け

        plugin = this;
        homesFile = new File(getDataFolder(), "home.yml"); //ファイル作成
        homes = YamlConfiguration.loadConfiguration(homesFile);

        new ConfigManager(this);
        new CommandManager(this);

        getLogger().info("プラグインは正常に起動しました。"); //プラグイン起動時にログを表示
    }

    @Override //プラグイン停止時の処理
    public void onDisable() {
        super.onDisable(); //絶対書け
        getLogger().info("プラグインは正常に停止しました。"); //プラグイン停止時にログを表示
    }

    public static JavaPlugin getPlugin() {
        return plugin; //pluginを返す
    }

    public static File getHomesFile() {
        return homesFile; //homesFileを返す
    }

    public static YamlConfiguration getHomes() {
        return homes; //homesを返す
    }
}
