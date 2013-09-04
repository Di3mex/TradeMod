package de.diemex.trademod;


import de.diemex.trademod.config.RootNode;
import static de.diemex.trademod.Message.*;

import de.diemex.trademod.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{

    private TradeMod plugin = null;


    public PlayerListener(TradeMod main)
    {
        plugin = main;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
        if (tradePlayer != null)
        {
            if (tradePlayer.isInTrade())
            {
                if (tradePlayer.isModifyingCurrency())
                {
                    tradePlayer.handleChatEvent(event.getMessage());
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
        if (tradePlayer != null)
        {
            if (tradePlayer.isInTrade())
            {
                tradePlayer.cancelTrade(tradePlayer, "Logged out.");
            } else
            {
                if (tradePlayer.isRequested())
                {
                    tradePlayer.cancelRequest(true, "Logged out.");
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
        if (event.getRightClicked() instanceof Player)
        {
            Player otherPlayer = (Player) event.getRightClicked();
            if (player.getItemInHand().getTypeId() == 0)
            { //is unarmed
                if (tradePlayer != null)
                {
                    TradePlayer otherTradePlayer = TradePlayer.getTradePlayer(otherPlayer);
                    if (tradePlayer.isInTrade())
                    {
                        if (tradePlayer.getPlayer().hasPermission(PermissionNode.QUICK_REOPEN.getNode()))
                        {
                            if (tradePlayer.getOtherPlayer() == otherTradePlayer)
                            {
                                if (plugin.getCFG().getBoolean(RootNode.RIGHT_CLICK_REOPEN))
                                {
                                    tradePlayer.getPlayer().openInventory(tradePlayer.getTrade().getTradeInventory());
                                }
                            }
                        }
                    } else if (tradePlayer.isRequested())
                    {
                        if (player.isSneaking())
                        {
                            tradePlayer.acceptRequest(otherTradePlayer);
                        }
                    }
                } else
                {
                    if (player.isSneaking())
                    {
                        if (plugin.getCFG().getBoolean(RootNode.SHIFT_RIGHT_INITIATE))
                        {
                            if (player.hasPermission(PermissionNode.RIGHTCLICK_REQUEST.getNode()))
                            {
                                if (!plugin.getCFG().getBoolean(RootNode.CREATIVE_TRADING))
                                {
                                    if (player.getGameMode() != GameMode.CREATIVE && otherPlayer.getGameMode() != GameMode.CREATIVE)
                                    {
                                        TradePlayer requested = new TradePlayer(plugin, otherPlayer);
                                        TradePlayer requester = new TradePlayer(plugin, player);
                                        if (requester.requestTrade(requested))
                                        {
                                            MSG_TRADE_REQUEST.send(requested.getPlayer(), player.getName());
                                            if (otherPlayer.hasPermission(PermissionNode.RIGHTCLICK_REQUEST.getNode()) && plugin.getCFG().getBoolean(RootNode.SHIFT_RIGHT_INITIATE))
                                                MSG_TRADE_REQUEST_RC_TIP.send(requested.getPlayer());
                                            MSG_TRADE_REQUEST_OTHER.send(requester.getPlayer(), otherPlayer.getName(), plugin.getCFG().getInt(RootNode.TIMEOUT));
                                        }
                                    } else
                                    {
                                        player.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
                                        otherPlayer.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
                                    }
                                } else
                                {
                                    TradePlayer requested = new TradePlayer(plugin, otherPlayer);
                                    TradePlayer requester = new TradePlayer(plugin, player);
                                    if (requester.requestTrade(requested))
                                    {
                                        MSG_TRADE_REQUEST.send(requested.getPlayer(), player.getName());
                                        if (otherPlayer.hasPermission(PermissionNode.RIGHTCLICK_REQUEST.getNode()) && plugin.getCFG().getBoolean(RootNode.SHIFT_RIGHT_INITIATE))
                                            MSG_TRADE_REQUEST_RC_TIP.send(requested.getPlayer());
                                        MSG_TRADE_REQUEST_OTHER.send(requester.getPlayer(), otherPlayer.getName(), plugin.getCFG().getInt(RootNode.TIMEOUT));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity();
        if (player != null)
        {
            TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
            if (tradePlayer != null)
            {
                if (tradePlayer.isInTrade())
                {
                    tradePlayer.getTrade().onDeath(tradePlayer);
                } else
                {
                    if (tradePlayer.isRequested())
                    {
                        tradePlayer.cancelRequest(true, "Died.");
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (player != null)
        {
            TradePlayer tradePlayer = TradePlayer.getTradePlayer(player);
            if (tradePlayer != null)
            {
                if (tradePlayer.isInTrade())
                {
                    Trade trade = tradePlayer.getTrade();
                    int radius = plugin.getCFG().getInt(RootNode.MAX_DISTANCE);
                    if (!plugin.withinRadius(trade.requested.getPlayer().getLocation(), trade.requester.getPlayer().getLocation(), radius))
                    {
                        tradePlayer.cancelTrade(tradePlayer, "Went out of range.");
                    }
                }
            }
        }
    }
}
