package de.diemex.trademod;


import de.diemex.trademod.config.RootNode;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class Trade
{

    public TradePlayer requester = null;
    public TradePlayer requested = null;

    public ArrayList<ItemStack> requesterItems = new ArrayList<ItemStack>();
    public ArrayList<ItemStack> requestedItems = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> requesterOvf = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> requestedOvf = new ArrayList<ItemStack>();

    public double requestedCur = 0;
    public double requesterCur = 0;

    private Inventory tradeI = null;
    private ScoreboardHandler sh = null;
    TradeMod plugin;


    public Trade(TradePlayer p1, TradePlayer p2, TradeMod plugin)
    {
        this.plugin = plugin;
        requester = p1;
        requested = p2;
        plugin.addTrade(this);
        if (plugin.hasEconomy())
        {
            if (this.plugin.getCFG().getBoolean(RootNode.SCOREBOARD_CURR))
            {
                sh = new ScoreboardHandler(p1, p2);
            }
        }
    }


    public void beginTrade()
    {
        tradeI = plugin.getServer().createInventory(null, 54, "Trade Screen");

        requester.getPlayer().openInventory(tradeI);
        requested.getPlayer().openInventory(tradeI);

        requester.setOtherPlayer(requested);
        requested.setOtherPlayer(requester);

        requester.sendMessage("You have the top portion of the screen.");
        requested.sendMessage("You have the bottom portion of the screen.");


        //Bounds for bottom/top part of the trade screen
        requester.minSlot = 2;
        requested.minSlot = 26;
        requester.maxSlot = 27;
        requested.maxSlot = 51;

        //CANCEL Buttons
        ItemStack redWool = new ItemStack(35, 1, (short) 14);
        ItemMeta rM = redWool.getItemMeta();
        rM.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "Cancel");
        redWool.setItemMeta(rM);
        requester.canSlot = 0;
        tradeI.setItem(0, redWool);
        requested.canSlot = 53;
        tradeI.setItem(53, redWool);

        //CONFIRM Buttons
        ItemStack greenWool = new ItemStack(35, 1, (short) 5);
        ItemMeta gW = greenWool.getItemMeta();
        gW.setDisplayName(ChatColor.GREEN + "" + ChatColor.ITALIC + "Confirm");
        greenWool.setItemMeta(gW);
        requester.accSlot = 1;
        tradeI.setItem(1, greenWool);
        requested.accSlot = 52;
        tradeI.setItem(52, greenWool);

        //CURRENCY Buttons
        ItemStack emerald = new ItemStack(388, 1);
        ItemMeta e = emerald.getItemMeta();
        e.setDisplayName(ChatColor.GOLD + "" + ChatColor.ITALIC + "Add/Remove Currency");
        emerald.setItemMeta(e);
        requester.curSlot = 2;
        tradeI.setItem(2, emerald);
        requested.curSlot = 51;
        tradeI.setItem(51, emerald);
    }


    public void addCurrency(TradePlayer p, double amount)
    {
        if (p != null)
        {
            if (amount > 0)
            {
                //TODO one validation method and a message with regex placeholders
                if (p.equals(requested))
                {
                    if (requested.getTrade().requester.hasConfirmed())
                    {
                        requested.getTrade().requester.setConfirmed(false);
                        requested.sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
                        requested.getTrade().requester.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
                    }
                    if (requestedCur + amount > plugin.getEconomy().getBalance(requested.getName()))
                    {
                        requestedCur = plugin.getEconomy().getBalance(requested.getName());
                        sh.updateBoard(requested, (int) requestedCur);
                        requested.sendMessage("You have added your entire balance of " + plugin.getEconomy().getBalance(requested.getName()) + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                        requester.sendMessage(requested.getName() + " has added their entire balance of " + plugin.getEconomy().getBalance(requested.getName()) + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                    } else
                    {
                        requestedCur += amount;
                        sh.updateBoard(requested, (int) requestedCur);
                        requested.sendMessage("You have added " + amount + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                        requester.sendMessage(requested.getName() + " has added " + amount + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                    }
                } else if (p.equals(requester))
                {
                    if (requester.getTrade().requested.hasConfirmed())
                    {
                        requester.getTrade().requested.setConfirmed(false);
                        requester.sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
                        requester.getTrade().requested.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
                    }
                    if (requesterCur + amount > plugin.getEconomy().getBalance(requester.getName()))
                    {
                        requesterCur = plugin.getEconomy().getBalance(requester.getName());
                        sh.updateBoard(requester, (int) requesterCur);
                        requester.sendMessage("You have added your entire balance of " + plugin.getEconomy().getBalance(requester.getName()) + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                        requested.sendMessage(requester.getName() + " has added their entire balance of " + plugin.getEconomy().getBalance(requester.getName()) + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                    } else
                    {
                        requesterCur += amount;
                        sh.updateBoard(requester, (int) requesterCur);
                        requester.sendMessage("You have added " + amount + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                        requested.sendMessage(requester.getName() + " has added " + amount + " " + plugin.getEconomy().currencyNameSingular() + " to the trade!");
                    }
                }
            }
        }
    }


    public void remCurrency(TradePlayer p, double amount)
    {
        if (p != null)
        {
            if (amount > 0)
            {
                if (p.equals(requested))
                {
                    if (requested.getTrade().requester.hasConfirmed())
                    {
                        requested.getTrade().requester.setConfirmed(false);
                        requested.sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
                        requested.getTrade().requester.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
                    }
                    if (requestedCur - amount > 0)
                    {
                        requestedCur -= amount;
                        sh.updateBoard(requested, (int) requestedCur);
                        requested.sendMessage("You have removed " + amount + " " + plugin.getEconomy().currencyNameSingular() + " from the trade, current amount left: " + requestedCur);
                        requester.sendMessage(requested.getName() + " has removed " + amount + " " + plugin.getEconomy().currencyNameSingular() + " from the trade, current amount left: " + requestedCur);
                    } else
                    {
                        requestedCur = 0;
                        sh.updateBoard(requested, 0);
                        requested.sendMessage("You have removed all currency from the offer.");
                        requester.sendMessage(requested.getName() + " has removed all currency from the offer.");
                    }
                } else if (p.equals(requester))
                {
                    if (requester.getTrade().requested.hasConfirmed())
                    {
                        requester.getTrade().requested.setConfirmed(false);
                        requester.sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
                        requester.getTrade().requested.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
                    }
                    if (requesterCur - amount > 0)
                    {
                        requesterCur -= amount;
                        sh.updateBoard(requester, (int) requesterCur);
                        requester.sendMessage("You have removed " + amount + " " + plugin.getEconomy().currencyNameSingular() + " from the trade, current amount left: " + requesterCur);
                        requested.sendMessage(requested.getName() + " has removed " + amount + " " + plugin.getEconomy().currencyNameSingular() + " from the trade, current amount left: " + requesterCur);
                    } else
                    {
                        requesterCur = 0;
                        sh.updateBoard(requester, 0);
                        requester.sendMessage("You have removed all currency from the offer.");
                        requested.sendMessage(requester.getName() + " has removed all currency from the offer.");
                    }
                }
            }
        }
    }


    public void confirm()
    {
        try
        {
            Inventory i = plugin.getServer().createInventory(null, 36);
            i.setContents(requester.getPlayer().getInventory().getContents());
            Inventory q = plugin.getServer().createInventory(null, 36);
            q.setContents(requested.getPlayer().getInventory().getContents());
            for (int a = requested.minSlot + 1; a < requested.maxSlot; a++)
            {
                ItemStack add = tradeI.getItem(a);
                if (add != null)
                {
                    HashMap<Integer, ItemStack> b = i.addItem(add);
                    if (b != null)
                    {
                        requestedOvf.addAll(b.values());
                    }
                }
            }
            for (int a = requester.minSlot + 1; a < requester.maxSlot; a++)
            {
                ItemStack add = tradeI.getItem(a);
                if (add != null)
                {
                    HashMap<Integer, ItemStack> b = q.addItem(add);
                    if (b != null)
                    {
                        requesterOvf.addAll(b.values());
                    }
                }
            }
            if (requestedOvf.isEmpty() && requesterOvf.isEmpty())
            {
                for (int a = requested.minSlot + 1; a < requested.maxSlot; a++)
                {
                    ItemStack add = tradeI.getItem(a);
                    if (add != null)
                    {
                        requestedItems.add(add);
                        requester.getPlayer().getInventory().addItem(add);
                    }
                }
                for (int a = requester.minSlot + 1; a < requester.maxSlot; a++)
                {
                    ItemStack add = tradeI.getItem(a);
                    if (add != null)
                    {
                        requesterItems.add(add);
                        requested.getPlayer().getInventory().addItem(add);
                    }
                }
                if (requestedCur > 0)
                {
                    if (plugin.getEconomy().getBalance(requested.getName()) >= requestedCur)
                    {
                        plugin.getEconomy().depositPlayer(requester.getName(), requestedCur);
                        plugin.getEconomy().withdrawPlayer(requested.getName(), requestedCur);
                        requester.sendMessage("You have received " + requestedCur + " " + plugin.getEconomy().currencyNameSingular() + " from " + requested.getName() + "!");
                        requested.sendMessage("You have payed " + requester.getName() + " " + requestedCur + " " + plugin.getEconomy().currencyNameSingular() + "!");
                    } else
                    {
                        requested.cancelTrade(requested, "Not enough " + plugin.getEconomy().currencyNameSingular() + "!");
                    }
                }
                if (requesterCur > 0)
                {
                    if (plugin.getEconomy().getBalance(requester.getName()) >= requesterCur)
                    {
                        plugin.getEconomy().depositPlayer(requested.getName(), requesterCur);
                        plugin.getEconomy().withdrawPlayer(requester.getName(), requesterCur);
                        requested.sendMessage("You have received " + requesterCur + " " + plugin.getEconomy().currencyNameSingular() + " from " + requester.getName() + "!");
                        requester.sendMessage("You have payed " + requested.getName() + " " + requesterCur + " " + plugin.getEconomy().currencyNameSingular() + "!");
                    } else
                    {
                        requester.cancelTrade(requester, "Not enough " + plugin.getEconomy().currencyNameSingular() + "!");
                    }
                }
                TradeLogger tL = new TradeLogger();
                tL.logTrade(this);
                requested.closeTrade();
                requester.closeTrade();
                requested.sendMessage("The trade was successful. You may now trade others.");
                requester.sendMessage("The trade was successful. You may now trade others.");
                requested.currencyModCancel();
                requester.currencyModCancel();
                TradePlayer.removePlayer(requested);
                TradePlayer.removePlayer(requester);
                i
                        = null;
                q = null;
                plugin.removeTrade(this);
                if (sh != null)
                    sh.closeBoard();
            } else
            {
                requester.setConfirmed(false);
                requested.setConfirmed(false);
                if (!requesterOvf.isEmpty())
                {
                    requester.sendMessage("You have too many items in your current inventory to confirm the trade. Please remove some items before attempting to trade the current inventory.");
                    requested.sendMessage(requester.getName() + " had too many items in their inventory to complete the trade.");
                }
                if (!requestedOvf.isEmpty())
                {
                    requested.sendMessage("You have too many items in your current inventory to confirm the trade. Please remove some items before attempting to trade the current inventory.");
                    requester.sendMessage(requested.getName() + " had too many items in their inventory to complete the trade.");
                }
                requesterOvf.clear();
                requestedOvf.clear();
                i = null;
                q = null;
                plugin.removeTrade(this);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public Inventory getTradeInventory()
    {
        return tradeI;
    }


    public void dropOverflow(Player p, ArrayList<ItemStack> a)
    {
        for (ItemStack i : a)
        {
            if (i != null)
            {
                World w = p.getWorld();
                w.dropItemNaturally(p.getLocation(), i);
            }
        }
    }


    public void tradeCanceled()
    {
        HashMap<Integer, ItemStack> a = new HashMap<Integer, ItemStack>();
        HashMap<Integer, ItemStack> b = new HashMap<Integer, ItemStack>();
        for (int c = requester.minSlot + 1; c < requester.maxSlot; c++)
        {
            ItemStack add = tradeI.getItem(c);
            if (add != null)
            {
                a.putAll(requester.getPlayer().getInventory().addItem(add));
            }
        }
        for (int c = requested.minSlot + 1; c < requested.maxSlot; c++)
        {
            ItemStack add = tradeI.getItem(c);
            if (add != null)
            {
                a.putAll(requested.getPlayer().getInventory().addItem(add));
            }
        }
        if (!a.isEmpty())
        {
            for (ItemStack i : a.values())
            {
                if (i != null)
                {
                    World w = requester.getPlayer().getWorld();
                    w.dropItemNaturally(requester.getPlayer().getLocation(), i);
                }
            }
        }
        if (!b.isEmpty())
        {
            for (ItemStack i : b.values())
            {
                if (i != null)
                {
                    World w = requested.getPlayer().getWorld();
                    w.dropItemNaturally(requested.getPlayer().getLocation(), i);
                }
            }
        }
        requested.currencyModCancel();
        requester.currencyModCancel();
        if (sh != null)
            sh.closeBoard();
    }


    public void onDeath(TradePlayer who)
    {
        if (who != null)
        {
            if (who.equals(requester))
            {
                HashMap<Integer, ItemStack> a = new HashMap<Integer, ItemStack>();
                for (int c = requester.minSlot + 1; c < requester.maxSlot; c++)
                {
                    ItemStack add = tradeI.getItem(c);
                    if (add != null)
                    {
                        World w = requester.getPlayer().getWorld();
                        w.dropItemNaturally(requester.getPlayer().getLocation(), add);
                    }
                }
                for (int c = requested.minSlot + 1; c < requested.maxSlot; c++)
                {
                    ItemStack add = tradeI.getItem(c);
                    if (add != null)
                    {
                        a.putAll(requested.getPlayer().getInventory().addItem(add));
                    }
                }
                if (!a.isEmpty())
                {
                    for (ItemStack i : a.values())
                    {
                        if (i != null)
                        {
                            World w = requested.getPlayer().getWorld();
                            w.dropItemNaturally(requested.getPlayer().getLocation(), i);
                        }
                    }
                }
                requester.cancelTrade(requester, "Died.");
            } else if (who.equals(requested))
            {
                HashMap<Integer, ItemStack> a = new HashMap<Integer, ItemStack>();
                for (int c = requested.minSlot + 1; c < requested.maxSlot; c++)
                {
                    ItemStack add = tradeI.getItem(c);
                    if (add != null)
                    {
                        World w = requested.getPlayer().getWorld();
                        w.dropItemNaturally(requested.getPlayer().getLocation(), add);
                    }
                }
                for (int c = requester.minSlot + 1; c < requester.maxSlot; c++)
                {
                    ItemStack add = tradeI.getItem(c);
                    if (add != null)
                    {
                        a.putAll(requester.getPlayer().getInventory().addItem(add));
                    }
                }
                if (!a.isEmpty())
                {
                    for (ItemStack i : a.values())
                    {
                        if (i != null)
                        {
                            World w = requester.getPlayer().getWorld();
                            w.dropItemNaturally(requester.getPlayer().getLocation(), i);
                        }
                    }
                }
                requested.cancelTrade(requested, "Died.");
            }
        }
    }
}