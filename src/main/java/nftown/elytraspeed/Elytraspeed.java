package nftown.elytraspeed;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Elytraspeed extends JavaPlugin implements Listener {

    private double maxSpeed; // 最大水平速度（米/秒）
    private boolean enablePlugin;
    private String warningMessage;
    private boolean showWarning;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigValues();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void reloadConfigValues() {
        maxSpeed = getConfig().getDouble("max-speed", 50.0);
        enablePlugin = getConfig().getBoolean("enable-plugin", true);
        warningMessage = getConfig().getString("warning-message", "&c速度已限制！");
        showWarning = getConfig().getBoolean("show-warning", true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!enablePlugin) return;

        Player player = event.getPlayer();
        if (player.hasPermission("elytraspeed.bypass") || !player.isGliding()) return;

        Vector velocity = player.getVelocity();

        Vector horizontal = new Vector(velocity.getX(), 0, velocity.getZ());
        double currentSpeed = horizontal.length() * 20; 
        if (currentSpeed > maxSpeed) {
            double scale = maxSpeed / currentSpeed;

            Vector newVelocity = horizontal.multiply(scale)
                    .setY(velocity.getY());

            player.setVelocity(newVelocity);

            if (showWarning) {
                player.sendMessage(warningMessage.replace('&', '§'));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("elytraspeed-reload")) {
            reloadConfig();
            reloadConfigValues();
            sender.sendMessage("配置已重载");
            return true;
        }
        return false;
    }
}
