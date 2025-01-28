//владелец плагина tikrate, по всем вопросам тг - @javaexploitcom
package com.holly.ban;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class HollyBans extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
    }

    @EventHandler
    public void hollyBan(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        String command = args[0].toLowerCase();

        if (command.equals("/ban") || command.equals("/tempban")) {
            event.setCancelled(true);

            if (args.length > 1) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                    Player admin = event.getPlayer();
                    String banMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("ban_message"));
                    admin.sendMessage(banMessage + " " + target.getName());

                    new BukkitRunnable() {
                        double t = 0;
                        double r = 1;
                        double rotate = Math.PI/4;
                        @Override
                        public void run() {
                            if (!target.isOnline()) {
                                cancel();
                            }

                            Location loc = target.getLocation().add(0, 1, 0);

                            for (double theta = 0; theta <= 2*Math.PI; theta += Math.PI/8) {
                                double x = r * Math.cos(theta + rotate);
                                double z = r * Math.sin(theta + rotate);
                                loc.getWorld().spawnParticle(Particle.END_ROD, loc.getX()+x, loc.getY() + t / 20, loc.getZ()+z, 0, 0, 0, 0, 1);
                            }

                            t += 0.2d;
                            rotate += Math.PI / 180;

                            if (t >= 20) {
                                cancel();
                            }
                        }
                    }.runTaskTimer(this, 0, 1);



                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event.getMessage().substring(1));
                        target.getWorld().spawnParticle(Particle.FLASH, target.getLocation(), 1);
                    }, 100);
                }
            }
        }
    }
}