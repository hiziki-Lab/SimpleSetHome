package network.hiziki.simplesethome.command.home;

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
import network.hiziki.simplesethome.Main;

/*
*
* HomeCommandExecutor
* 作成者：hi_kun_JPN
* コマンドを処理するクラス
*
* */

public class HomeCommandExecutor implements CommandExecutor {
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
            }
            else { //サブコマンドが設定されていたら
                int homeNum = Integer.parseInt(args[0]); //args[0]を数字に変換

                if (homeNum > config.get_MAX_HOME || homeNum == 0) { //サブコマンドが設定されている数を超えている or 0だったら
                    new Util().prefix(p, ChatColor.RED + "サブコマンドは 1~" + config.get_MAX_HOME + " までしかありません。"); //送信
                }
                else { //サブコマンドが正常な場合
                    if (homes.getString("Homes." + p.getUniqueId() + "." + homeNum) == null) { //ホームがなかったら
                        new Util().prefix(p, ChatColor.RED + "ホーム " + homeNum + " は設定されていません。"); //エラーをプレイヤーに送信
                    } else { //ホームがあったら
                        if (config.get_ENABLE_TELEPORT_DELAY) { //遅延がありだったら
                            teleportCountDown(p, homeNum); //teleportCountDownメソッドに転送
                        } else { //遅延がなしだったら
                            teleportHome(p, homeNum); //teleportHomeメソッドに転送
                        }
                    }
                }
            }
        }
        return true; //コマンドが実行されたとして処理
    }

    private int count; //カウント用変数

    private void teleportCountDown(Player p, int num) { //テレポート遅延用メソッド
        count = config.get_TELEPORT_DELAY; //代入

        double x = p.getLocation().getX(); //ロケーションを保存
        double y = p.getLocation().getY();
        double z = p.getLocation().getZ();

        new BukkitRunnable() { //スケジューラー
            @Override
            public void run() { //この中が回される
                if (config.get_MOVE_CANCEL) { //移動したらキャンセルされる設定になっていたら
                    if (x == p.getLocation().getX() && y == p.getLocation().getY() && z == p.getLocation().getZ()) {
                        if (count <= 0) { //カウントが0になったら
                            teleportHome(p, num); //teleportHomeメソッドに転送
                            cancel(); //スケジューラーをキャンセルする
                            return; //メソッドから抜ける
                        }
                        count--; //カウントを1引く
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f); //再生
                    } else { //プレイヤーがカウント中に動いたら
                        new Util().prefix(p, ChatColor.RED + "移動したためテレポートがキャンセルされました。"); //プレイヤーに送信
                        cancel(); //スケジューラーから抜ける
                    }
                } else {  //移動してもキャンセルされない設定になっていたら
                    if (count <= 0) { //カウントが0になったら
                        teleportHome(p, num); //teleportHomeメソッドに転送
                        cancel(); //スケジューラーをキャンセルする
                        return; //メソッドから抜ける
                    }
                    count--; //カウントを1引く
                    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f); //再生
                }
            }
        }.runTaskTimer(plugin, 0, 20); //20Tick(20tick = 1秒) 1秒ごとに回す
    }

    private void teleportHome(Player p, int num) { //テレポート用メソッド
        p.teleport(homes.getLocation("Homes." + p.getUniqueId() + "." + num + ".Location")); //ホームにテレポートする

        new Util().prefix(p, ChatColor.AQUA + "ホームにテレポートしました。"); //プレイヤーにメッセージを送信

        if (config.get_ENABLE_TELEPORT_SOUND) { //設定されていたら
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F); //再生
        }
    }
}
