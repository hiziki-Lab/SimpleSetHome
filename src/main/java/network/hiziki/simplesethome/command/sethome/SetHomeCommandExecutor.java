package network.hiziki.simplesethome.command.sethome;

import network.hiziki.simplesethome.Main;
import network.hiziki.simplesethome.Util;
import network.hiziki.simplesethome.config.ConfigFile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/*
 *
 * SetHomeCommandExecutor
 * 作成者：hi_kun_JPN
 * コマンドを処理するクラス
 *
 * */

public class SetHomeCommandExecutor implements CommandExecutor {
    private final JavaPlugin plugin = Main.getPlugin(); //JavaPlugin

    private final YamlConfiguration homes = Main.getHomes(); //ホームファイル

    private final ConfigFile config = new ConfigFile(); //設定

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { //プレイヤーじゃなかったら
            sender.sendMessage("コマンドを実行できるのはプレイヤーのみです。"); //エラーを送信
        } else { //プレイヤーだったら
            Player p = (Player) sender;

            if (args.length == 0) { //サブコマンドが設定されていなかったら
                new Util().prefix(p, ChatColor.RED + "サブコマンドが設定されていません。"); //プレイヤーにメッセージを送信
            } else { //サブコマンドが設定されていたら
                int homeNum = Integer.parseInt(args[0]); //args[0]を数字に変換

                if (homeNum > config.get_MAX_HOME || homeNum == 0) { //サブコマンドが設定されている数を超えている or 0だったら
                    new Util().prefix(p, ChatColor.RED + "サブコマンドは 1~" + config.get_MAX_HOME + " までしかありません。"); //送信
                } else { //サブコマンドが正常な数の場合
                    if (config.get_ENABLE_SET_HOME_DELAY) { //遅延がありだったら
                        setHomeCountDown(p, homeNum); //setHomeCountDownメソッドに転送
                    } else { //遅延なしだったら
                        setHome(p, homeNum); //setHomeメソッドに転送
                    }
                }
            }
        }
        return true; //コマンドが実行されたとして処理
    }

    private int count; //カウント用変数

    private void setHomeCountDown(Player p, int num) { //テレポート遅延用メソッド
        count = config.get_SET_HOME_DELAY; //代入

        double x = p.getLocation().getX(); //ロケーションを保存
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();

        new BukkitRunnable() { //スケジューラー
            @Override
            public void run() { //この中が回される
                if (config.get_MOVE_CANCEL) { //移動したらキャンセルされる設定になっていたら
                    if (x == p.getLocation().getX() && y == p.getLocation().getY() && z == p.getLocation().getZ()) {
                        if (count <= 0) { //カウントが0になったら
                            setHome(p, num); //setHomeメソッドに転送
                            cancel(); //スケジューラーをキャンセルする
                            return; //メソッドから抜ける
                        }
                        count--; //カウントを1引く
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f); //再生
                    } else { //プレイヤーがカウント中に動いたら
                        new Util().prefix(p, ChatColor.RED + "移動したためホームの設定がキャンセルされました。"); //プレイヤーに送信
                        cancel(); //スケジューラーから抜ける
                    }
                } else { //移動してもキャンセルされない設定になっていたら
                    if (count <= 0) { //カウントが0になったら
                        setHome(p, num); //setHomeメソッドに転送
                        cancel(); //スケジューラーをキャンセルする
                        return; //メソッドから抜ける
                    }
                    count--; //カウントを1引く
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f); //再生
                }
            }
        }.runTaskTimer(plugin, 0, 20); //20Tick(20tick = 1秒) 1秒ごとに回す
    }

    private void setHome(Player p, int num) { //ホーム設定メソッド
        homes.set("Homes." + p.getUniqueId() + "." + num + ".Location", p.getLocation()); //ホームを設定

        new Util().prefix(p, ChatColor.AQUA + "ホームを設定しました。"); //プレイヤーに送信する

        if (config.get_ENABLE_SET_HOME_SOUND) { //効果音を再生
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1); //再生
        }
        new Util().saveFile(); //設定したファイルを保存
    }
}
