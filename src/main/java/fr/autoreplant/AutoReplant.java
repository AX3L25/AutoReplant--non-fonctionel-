package fr.autoreplant;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoReplant extends JavaPlugin implements Listener {

    private boolean autoReplantEnabled = true;
    private boolean onlyReplantHarvested = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("replant").setExecutor((sender, command, label, args) -> {
            if (args.length == 0) {
                autoReplantEnabled = !autoReplantEnabled;
                sender.sendMessage("AutoRePlant est maintenant " + (autoReplantEnabled ? "activé" : "désactivé"));
            } else if (args[0].equalsIgnoreCase("harvested")) {
                onlyReplantHarvested = !onlyReplantHarvested;
                sender.sendMessage("AutoRePlant va maintenant " + (onlyReplantHarvested ? "seulement" : "aussi") + " replanter les cultures récoltées");
            }
            return true;
        });
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.WHEAT || block.getType() == Material.CARROTS || block.getType() == Material.POTATOES) {
            Ageable ageable = (Ageable) block.getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                ageable.setAge(0);
                block.setBlockData(ageable);

                // Modification ici: la graine est posée sur le bloc où la pousse a été cassée
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(block.getType());
                        block.setBlockData(ageable);
                        ItemStack seeds = new ItemStack(Material.WHEAT_SEEDS, 1);
                        event.getPlayer().getInventory().addItem(seeds);
                    }
                }.runTaskLater(this, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (autoReplantEnabled && onlyReplantHarvested) {
            Block block = event.getClickedBlock();
            if (block != null && (block.getType() == Material.WHEAT || block.getType() == Material.CARROTS || block.getType() == Material.POTATOES)) {
                Ageable ageable = (Ageable) block.getBlockData();
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    ageable.setAge(0);
                    block.setBlockData(ageable);
                }
            }
        }
    }
}
