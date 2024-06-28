package net.skullian.torrent.skyfactions.util;

import net.milkbowl.vault.economy.Economy;
import net.skullian.torrent.skyfactions.SkyFactionsReborn;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {

    public Economy economy = null;


    public void init(SkyFactionsReborn instance) {
        RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new RuntimeException("Failed to find Vault Economy!");
        }

        economy = rsp.getProvider();
    }

    public boolean hasEnoughMoney(Player player, int count) {
        return economy.getBalance(player) >= count;
    }
}
