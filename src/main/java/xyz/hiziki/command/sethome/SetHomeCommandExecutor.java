package xyz.hiziki.command.sethome;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.hiziki.Main;
import xyz.hiziki.config.ConfigFile;
import xyz.hiziki.util.Prefix;
import xyz.hiziki.util.SaveFile;

public class SetHomeCommandExecutor implements CommandExecutor
{
    private final JavaPlugin plugin = Main.getPlugin();

    private final YamlConfiguration homes = Main.getHomes();

    private final ConfigFile config = new ConfigFile();

    private int count;

    private double x;

    private double y;

    private double z;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player p)) //プレイヤーかどうかを確認 - プレイヤーじゃなかったら
        {
            sender.sendMessage("コマンドを実行出来るのはプレイヤーのみです。"); //エラーを送信
        }
        else //プレイヤーだったら
        {
            if (args.length == 0) //args が0だったら = サブコマンドが設定されていなかったら
            {
                new Prefix(p, ChatColor.RED + "サブコマンドが設定されていません。"); //エラーをプレイヤーに送信
            }
            else //サブコマンドが設定されていたら
            {
                int homeNum = Integer.parseInt(args[0]);

                if (homeNum > config.getMAX_HOME() || homeNum == 0) //サブコマンドが設定されている数を超えている or 0だったら
                {
                    new Prefix(p, ChatColor.RED + "サブコマンドは 1~" + config.getMAX_HOME() +
                            " までしかありません。"); //エラーをプレイヤーに送信
                }
                else //サブコマンドが設定されている数以内だったら
                {
                    if (config.getENABLE_SET_HOME_DELAY())
                    {
                        setHomeCountDown(p, homeNum);
                    }
                    else
                    {
                        setHome(p, homeNum);
                    }
                }
            }
        }
        return true; //return false だったら実行されずにチャットとして送信されることになる。
    }

    private void setHomeCountDown(Player p, int num)
    {
        count = config.getSET_HOME_DELAY();

        x = p.getLocation().getX();
        y = p.getLocation().getY();
        z = p.getLocation().getZ();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (x == p.getLocation().getX() && y == p.getLocation().getY() && z == p.getLocation().getZ())
                {
                    if (count <= 0)
                    {
                        setHome(p, num);
                        cancel();
                        return;
                    }
                    count--;
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.5F); //再生
                }
                else
                {
                    new Prefix(p, ChatColor.RED + "移動したためホームの設定がキャンセルされました。"); //プレイヤーに送信
                    cancel(); //スケジューラーから抜ける
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void setHome(Player p, int num) //ホーム設定メソッド
    {
        homes.set("Homes." + p.getUniqueId() + "." + num + ".Location", p.getLocation()); //ホームを設定

        if (config.getENABLE_SET_HOME_MESSAGE()) //設定ファイルでメッセージがtrueになっていたら
        {
            if (config.getSET_HOME_MESSAGE() != null) //メッセージがあるかどうかを確認して
            {
                new Prefix(p, ChatColor.AQUA + config.getSET_HOME_MESSAGE()); //プレイヤーに送信する
            }
        }
        if (config.getENABLE_SET_HOME_SOUND()) //効果音を再生
        {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1); //再生
        }

        new SaveFile(); //設定したファイルを保存

        //こんな感じで保存される
        //Homes:
        //  7aa912df-eeed-49d6-814e-5c8994d527f3:
        //    '1':
        //    location:
        //      ==: org.bukkit.Location
        //      world: world
        //      x: 214.30000001192093
        //      y: 65.0
        //      z: -95.69999998807907
        //      pitch: 16.300346
        //      yaw: -186.54565
    }
}