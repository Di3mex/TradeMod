package de.diemex.trademod;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        Inventory i = e.getInventory();
        TradePlayer tP = TradePlayer.getTradePlayer(p);
        if (tP != null)
        {
            if (tP.isInTrade())
            {
                if (i.getName().equalsIgnoreCase("trade screen"))
                {
                    tP.handleClickEvent(e);
                }
            }
        }
    }


    public void onInventoryDrag(InventoryDragEvent e)
    {
        Inventory i = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        TradePlayer tP = TradePlayer.getTradePlayer(p);
        if (tP != null)
        {
            if (tP.isInTrade())
            {
                if (i.getName().equalsIgnoreCase("trade screen"))
                {
                    e.setCancelled(true);
                }
            }
        }

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent e)
    {
        Inventory i = e.getInventory();
        Player p = (Player) e.getPlayer();
        TradePlayer tP = TradePlayer.getTradePlayer(p);
        if (tP != null)
        {
            if (tP.isInTrade())
            {
                if (i.getName().equalsIgnoreCase("trade screen"))
                {
                    if (tP.tradeInv == null)
                    {
                        tP.tradeInv = i;
                    } else if (tP.isModifyingCurrency())
                    {
                        tP.cancelCurrencyModification();
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent e)
    {
        Inventory i = e.getInventory();
        Player p = (Player) e.getPlayer();
        TradePlayer tP = TradePlayer.getTradePlayer(p);
        if (tP != null)
        {
            if (tP.isInTrade())
            {
                if (tP.hasConfirmed())
                {
                    if (i.getName().equalsIgnoreCase("trade screen"))
                    {
                        Message.MSG_UNCONFIRM_CLOSE_INV.send(tP.getPlayer());
                        tP.setConfirmed(false);
                    }
                }
            }
        }
    }

}
