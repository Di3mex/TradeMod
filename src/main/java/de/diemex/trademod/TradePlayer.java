package de.diemex.trademod;


import de.diemex.trademod.config.RootNode;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import static de.diemex.trademod.Message.*;
import java.util.ArrayList;

public class TradePlayer
{
    private TradeMod plugin = null;
    public boolean inTrade = false;
    private String playerName = null;

    private TradePlayer requesterPlayer = null;
    private TradePlayer requestedPlayer = null;
    private TradePlayer otherPlayer = null;

    public Trade trade = null;
    //TODO remove the static bullshit
    private static ArrayList<TradePlayer> players = new ArrayList<TradePlayer>();
    public Inventory tradeInv = null;
    public int maxSlot = -1;
    public int minSlot = -1;
    public int accSlot = -1;
    public int canSlot = -1;

    public int curSlot = -1;
    private boolean confirmed = false;
    private boolean modCurrency = false;
    private int taskId = -1;


    public TradePlayer(TradeMod tm, Player player)
    {
        playerName = player.getName();
        players.add(this);
        plugin = tm;
    }


    public boolean requestTrade(TradePlayer oP)
    {
        if (oP != null)
        {
            requestedPlayer = oP;
            oP.requesterPlayer = this;
            timeRequest(plugin.getCFG().getInt(RootNode.TIMEOUT));
            return true;
        }
        return false;
    }


    public void openTrade()
    {
        //TODO test if player logs out etc
        plugin.getServer().getPlayer(playerName).openInventory(trade.getTradeInventory());
    }


    public void handleChatEvent(String msg)
    {
        try
        {
            if (plugin.hasEconomy())
            {
                double bal = plugin.getEconomy().getBalance(playerName);
                double amount;
                if (bal > 0)
                {
                    if (!msg.startsWith("-"))
                    {
                        //TODO RegexHelper
                        amount = Double.parseDouble(msg);
                        if (!(amount > bal))
                        {
                            //TODO one method sub/add
                            trade.addCurrency(this, amount);
                            openTrade();
                            modCurrency = false;
                        } else
                        {
                            Message.ERR_NOT_ENOUGH_CURRENCY.send(this.getPlayer(), amount, plugin.getEconomy().currencyNameSingular());
                        }
                    } else
                    {
                        amount = Double.parseDouble(msg.substring(1));
                        trade.remCurrency(this, amount);
                        openTrade();
                        modCurrency = false;
                    }
                } else
                {
                    ERR_NO_CURRENCY.send(getPlayer(), plugin.getEconomy().currencyNameSingular());
                }
            } else
            {
                ERR_NO_ECONOMY_ACTIVE.send(getPlayer());
            }
        } catch (Exception e)
        {
            ERR_CURRENCY_INVALID_INPUT.send(getPlayer());
        }
    }


    //TODO centralize
    public void handleClickEvent(InventoryClickEvent event)
    {
        try
        {
            TradeLogger tradeLogger = new TradeLogger();
            if (event.isShiftClick())
            {
                Message.ERR_SHIFT_CLICK_DISABLED.send(getPlayer());
                event.setCancelled(true);
                return;
            }
            int slot = event.getRawSlot();
            ItemStack i = event.getCurrentItem();
            TradePlayer oP = getOtherPlayer();
            if (slot > minSlot && slot < maxSlot)
            {
                if (hasConfirmed())
                {
                    Message.ERR_UNCONFIRM_BEFORE_EDIT.send(getPlayer());
                    event.setCancelled(true);
                } else
                {
                    if (oP.hasConfirmed())
                    {
                        if ((event.getCursor() != null && event.getCursor().getType() != Material.AIR) || i.getType() != Material.AIR)
                        {
                            oP.setConfirmed(false);
                            Message.MSG_UNCONFIRM_AFTER_EDIT.send(getPlayer());
                            Message.MSG_UNCONFIRM_AFTER_EDIT_OTHER.send(oP.getPlayer());
                        }
                    }
                    //TODO IMPLEMENT blacklist configuration
                    if (!true)
                    {
                        Message.ERR_UNTRADABLE.send(getPlayer());
                        event.setCancelled(true);
                    }
                }
            } else
            {
                if (slot > 53)
                {
                    if (!hasConfirmed())
                    {
                        return; // Player inv.
                    } else
                    {
                        ERR_UNCONFIRM_BEFORE_EDIT.send(getPlayer());
                        event.setCancelled(true);
                    }
                }
                if (slot == curSlot)
                {
                    tradeLogger.logClicks(getName() + " clicked currency slot.");
                    if (plugin.getServer().getPlayer(playerName).hasPermission("trademod.currency"))
                    {
                        if (!hasConfirmed())
                        {
                            closeTrade();
                            MSG_CURRENCY_MODIFY_PART1.send(getPlayer());
                            MSG_CURRENCY_MODIFY_PART2.send(getPlayer(), plugin.getCFG().getInt(RootNode.CURRENCY_TIMEOUT));
                            modCurrency = true;
                            currencyModifyTimer(plugin.getCFG().getInt(RootNode.CURRENCY_TIMEOUT));
                        } else
                        {
                            ERR_UNCONFIRM_BEFORE_EDIT.send(getPlayer());
                            event.setCancelled(true);
                        }
                        if (oP.hasConfirmed())
                        {
                            oP.setConfirmed(false);
                            MSG_UNCONFIRM_AFTER_EDIT.send(getPlayer());
                            MSG_UNCONFIRM_AFTER_EDIT_OTHER.send(oP.getPlayer());
                        }
                    } else
                    {
                        ERR_NO_PERM_CURRENCY.send(getPlayer());
                    }
                }
                if (i != null)
                {
                    if (!(event.isRightClick() && event.isShiftClick()))
                    {
                        if (i.getType() == Material.WOOL)
                        {
                            if (slot == accSlot)
                            {
                                tradeLogger.logClicks(getName() + " confirmed offer.");
                                if (!oP.hasConfirmed())
                                {
                                    // if(e.getCursor() == null) {
                                    if (i.getDurability() == 5)
                                    {
                                        i.setDurability((short) 3);
                                        setConfirmed(true);
                                    } else if (i.getDurability() == 3)
                                    {
                                        setConfirmed(false);
                                    }
                                    // }
                                } else if (oP.hasConfirmed())
                                {
                                    trade.confirm();
                                    tradeLogger.logClicks(getName() + " accepted trade.");
                                }
                            }
                            if (slot == canSlot)
                            {
                                cancelTrade(this, "Manually canceled.");
                                tradeLogger.logClicks(getName() + " clicked cancel.");
                            }
                        }
                    }
                }
                updateScreen(); //Necessary until bukkit implements better syncing.
                event.setCancelled(true);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public Player getPlayer()
    {
        return plugin.getServer().getPlayer(playerName);
    }


    public boolean isModifyingCurrency()
    {
        return modCurrency;
    }


    public void currencyModCancel()
    {
        modCurrency = false;
    }


    public void cancelCurrencyModification()
    {
        modCurrency = false;
        MSG_CURRENCY_CANCELED_REOPEN.send(getPlayer());
        plugin.getServer().getScheduler().cancelTask(taskId);
    }


    public void updateScreen()
    {
        //TODO this is actually hardly ever needed, something must be going wrong here
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @SuppressWarnings("deprecation")
            @Override
            public void run()
            {
                plugin.getServer().getPlayer(playerName).updateInventory();
            }
        }, 3);
    }


    public void closeTrade()
    {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                plugin.getServer().getPlayer(playerName).closeInventory();
            }
        });
    }


    public void currencyModifyTimer(int seconds)
    {
        long s = seconds * 20;
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                if (modCurrency)
                {
                    modCurrency = false;
                    MSG_RESUME_TRADE.send(getPlayer());
                    plugin.getServer().getPlayer(playerName).openInventory(trade.getTradeInventory());
                }
            }
        }, s);
    }


    public boolean hasConfirmed()
    {
        return confirmed;
    }


    public void setConfirmed(boolean bool)
    {
        confirmed = bool;
        if (!bool)
        {
            trade.getTradeInventory().getItem(accSlot).setDurability((short) 5);
            if (trade.requested.equals(this))
            {
                MSG_UNCONFIRM_OTHER.send(trade.requester.getPlayer(), getName());
                MSG_UNCONFIRM.send(trade.requested.getPlayer());
            } else
            {
                MSG_UNCONFIRM_OTHER.send(trade.requested.getPlayer(), getName());
                MSG_UNCONFIRM.send(trade.requester.getPlayer());
            }
        } else
        {
            trade.getTradeInventory().getItem(accSlot).setDurability((short) 3);
            if (trade.requested.equals(this))
            {
                if (!trade.requester.hasConfirmed())
                {
                    MSG_CONFIRM_OTHER.send(trade.requester.getPlayer(), getName());
                    MSG_CONFIRM.send(trade.requested.getPlayer());
                } else
                {
                    trade.confirm();
                }
            } else
            {
                if (!trade.requested.hasConfirmed())
                {
                    MSG_CONFIRM_OTHER.send(trade.requested.getPlayer(), getName());
                    MSG_CONFIRM.send(trade.requester.getPlayer());
                } else
                {
                    trade.confirm();
                }
            }
        }
    }


    public void timeRequest(int seconds)
    {
        try
        {
            long s = seconds * 20;
            taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
            { //obtain task id so we can cancel it later on if need be.
                @Override
                public void run()
                {
                    cancelRequest(true, "Request timed out.");
                }
            }, s);
        } catch (Exception e)
        {
            // Request was already accepted, or something stupid happened.
        }
    }


    public void acceptRequest(TradePlayer oP)
    {
        if (oP != null)
        {
            if (requesterPlayer != null)
            {
                requesterPlayer = null;
                oP.requestedPlayer = null;
                inTrade = true;
                oP.inTrade = true;
                trade = new Trade(oP, this, plugin);
                oP.trade = trade;
                trade.beginTrade();
            }
        }
    }


    public void setOtherPlayer(TradePlayer who)
    {
        otherPlayer = who;
    }


    public TradePlayer getOtherPlayer()
    {
        return otherPlayer;
    }


    public void cancelTrade(TradePlayer who, String reason)
    {
        reason = ChatColor.RED + reason;
        if (who.equals(this))
        {
            if (trade.requested.equals(this))
            {
                MSG_TRADE_CANCEL_OTHER.send(trade.requester.getPlayer(), getName(), reason);
                MSG_TRADE_CANCEL.send(getPlayer(), reason);
            } else
            {
                MSG_TRADE_CANCEL_OTHER.send(trade.requested.getPlayer(), getName(), reason);
                MSG_TRADE_CANCEL.send(getPlayer(), reason);
            }
        } else
        {
            MSG_TRADE_CANCEL.send(who.getPlayer(), reason);
            MSG_TRADE_CANCEL_OTHER.send(getPlayer(), who.getName(), reason);
        }
        if (!reason.contains("Died"))
        {
            trade.tradeCanceled();
        }
        trade.requested.closeTrade();
        trade.requester.closeTrade();
        players.remove(trade.requested);
        players.remove(trade.requester);
        players.remove(this);
        trade.requested = null;
        trade.requester = null;
        plugin.removeTrade(trade);
    }


    public Trade getTrade()
    {
        return trade;
    }


    public String getName()
    {
        return playerName;
    }


    public boolean isRequested()
    {
        if (requesterPlayer != null)
        {
            return true;
        }
        return false;
    }


    public boolean isInTrade()
    {
        return inTrade;
    }


    public void cancelRequest(boolean bool, String reason)
    {
        try
        {
            reason = ChatColor.RED + reason;
            if (bool)
            {
                if (requestedPlayer != null)
                {
                    MSG_TRADE_DECLINE.send(getPlayer(), requestedPlayer.getName(), reason);
                    MSG_TRADE_DECLINE_OTHER.send(requestedPlayer.getPlayer(), getName(), reason);
                } else
                {
                    MSG_TRADE_DECLINE.send(requesterPlayer.getPlayer(), getName(), reason);
                    MSG_TRADE_DECLINE_OTHER.send(getPlayer(), requesterPlayer.getName(), reason);
                }
            }
            if (requestedPlayer != null)
            {
                players.remove(requestedPlayer);
            } else
            {
                players.remove(requesterPlayer);
            }
            players.remove(this);
            requestedPlayer = null;
            requesterPlayer = null;
        } catch (NullPointerException nE)
        {
        }
    }


    public TradePlayer getRequester()
    {
        if (isRequested())
        {
            return requesterPlayer;
        } else
        {
            return null;
        }
    }


    public TradePlayer getRequested()
    {
        return requestedPlayer;
    }


    public static void removePlayer(TradePlayer player)
    {
        players.remove(player);
    }


    public static TradePlayer getTradePlayer(Player player)
    {
        return getTradePlayer(player.getName());
    }


    public static TradePlayer getTradePlayer(String name)
    {
        Validate.notEmpty(name);
        for (TradePlayer p : players)
        {
            if (p.playerName.equalsIgnoreCase(name))
            {
                return p;
            }
        }
        return null;
    }
}
