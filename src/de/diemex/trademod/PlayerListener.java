package de.diemex.trademod;

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

public class PlayerListener implements Listener {

	private TradeMod np = null;

	public PlayerListener(TradeMod main) {
		np = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		TradePlayer tP = TradePlayer.getTradePlayer(p);
		if (tP != null) {
			if (tP.isInTrade()) {
				if (tP.isModifyingCurrency()) {
					tP.handleChatEvent(e.getMessage());
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogout(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		TradePlayer tP = TradePlayer.getTradePlayer(p);
		if (tP != null) {
			if (tP.isInTrade()) {
				tP.cancelTrade(tP, "Logged out.");
			} else {
				if (tP.isRequested()) {
					tP.cancelRequest(true, "Logged out.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		TradePlayer tP = TradePlayer.getTradePlayer(p);
		if (e.getRightClicked() instanceof Player) {
			Player rP = (Player) e.getRightClicked();
			if (p.getItemInHand().getTypeId() == 0) { //is unarmed	
				if (tP != null) {
					TradePlayer rTP = TradePlayer.getTradePlayer(rP);
					if (tP.isInTrade()) {
						if (tP.getPlayer().hasPermission("trademod.quickreopen")) {
							if (tP.getOtherPlayer() == rTP) {
								if (ConfigLoader.getReopenEnabled()) {
									tP.getPlayer().openInventory(tP.getTrade().getTradeInventory());
								}
							}
						}
					} else if (tP.isRequested()) {
						if (p.isSneaking()) {
							tP.acceptRequest(rTP);
						}
					}
				} else {
					if (p.isSneaking()) {
						if (ConfigLoader.getRequestEnabled()) {
							if (p.hasPermission("trademod.rightclickrequest")) {
								if (!ConfigLoader.creativeToSurv()) {
									if (p.getGameMode() != GameMode.CREATIVE && rP.getGameMode() != GameMode.CREATIVE) {
										TradePlayer requested = new TradePlayer(np, rP);
										TradePlayer requester = new TradePlayer(np, p);
										if (requester.requestTrade(requested)) {
											requested.sendMessage(p.getName() + " would like to trade with you, type /tm acc to accept the request, or type /tm dec to decline it.");
											if (rP.hasPermission("trademod.rightclickrequest") && ConfigLoader.getRequestEnabled())
												requested.sendMessage("You can also sneak and right click, while unarmed, on the other player to accept the request.");
											requester.sendMessage("You have requested " + rP.getName() + " to trade with you. The request will automatically cancel in " + ConfigLoader.getTimeout() + " seconds");
										}
									} else {
										p.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
										rP.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
									}
								} else {
									TradePlayer requested = new TradePlayer(np, rP);
									TradePlayer requester = new TradePlayer(np, p);
									if (requester.requestTrade(requested)) {
										requested.sendMessage(p.getName() + " would like to trade with you, type /tm acc to accept the request, or type /tm dec to decline it.");
										if (rP.hasPermission("trademod.rightclickrequest") && ConfigLoader.getRequestEnabled())
											requested.sendMessage("You can also sneak and right click, while unarmed, on the other player to accept the request.");
										requester.sendMessage("You have requested " + rP.getName() + " to trade with you. The request will automatically cancel in " + ConfigLoader.getTimeout() + " seconds");
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
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if (p != null) {
			TradePlayer tP = TradePlayer.getTradePlayer(p);
			if (tP != null) {
				if (tP.isInTrade()) {
					tP.getTrade().onDeath(tP);
				} else {
					if (tP.isRequested()) {
						tP.cancelRequest(true, "Died.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (p != null) {
			TradePlayer tP = TradePlayer.getTradePlayer(p);
			if (tP != null) {
				if (tP.isInTrade()) {
					Trade t = tP.getTrade();
					int radius = ConfigLoader.getMaxDistance();
					if (!np.withinRadius(t.requested.getPlayer().getLocation(), t.requester.getPlayer().getLocation(), radius)) {
						tP.cancelTrade(tP, "Went out of range.");
					}
				}
			}
		}
	}
}
