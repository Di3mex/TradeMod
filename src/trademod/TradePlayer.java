package trademod;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TradePlayer {

	public boolean inTrade = false;
	private Player player = null;
	private TradePlayer requesterPlayer = null;
	private TradePlayer requestedPlayer = null;
	public Trade trade = null;
	public static ArrayList<TradePlayer> players = new ArrayList<TradePlayer>();
	public Inventory tradeInv = null;
	private TradeMod trm = null;
	public int maxSlot = -1;
	public int minSlot = -1;
	public int accSlot = -1;
	public int canSlot = -1;
	public int curSlot = -1;
	private boolean confirmed = false;
	private TradePlayer otherPlayer = null;
	private boolean modCurrency = false;
	private int w = -1;

	public TradePlayer(TradeMod tm, Player p) {
		player = p;
		players.add(this);
		trm = tm;
		// TODO Auto-generated constructor stub
	}

	public boolean requestTrade(TradePlayer oP) {
		if (oP != null) {
			requestedPlayer = oP;
			oP.requesterPlayer = this;
			timeRequest(ConfigLoader.getTimeout());
			return true;
		}
		return false;
	}
	
	public void openTrade() {
		player.openInventory(trade.getTradeInventory());
	}
	public void handleChatEvent(String msg) {
		try {	
			if (TradeMod.economy != null) {
				double bal = TradeMod.economy.getBalance(player.getName());
				double amount = 0;
				if (bal > 0) {
					if (!msg.startsWith("-")) {	
						amount = Double.parseDouble(msg);
						if (!(amount > bal)) {
							trade.addCurrency(this, amount);
							openTrade();
							modCurrency = false;
						} else {
							sendMessage("You do not have " + amount + " " + TradeMod.economy.currencyNameSingular() + "!");
						}
					} else {
						amount = Double.parseDouble(msg.substring(1));
						trade.remCurrency(this, amount);
						openTrade();
						modCurrency = false;
					}
				} else {
					sendMessage("You do not have any " + TradeMod.economy.currencyNameSingular() + "!");
				}
			} else {
				sendMessage("This server does not support currency in trades!");
			}
		} catch (Exception e) {
			sendMessage("You must enter a number. Negative numbers indicate a removal of currency from the offer");
			sendMessage("while positive numbers indicate an addition to your offer.");
		}
	}
	public void handleClickEvent(InventoryClickEvent e) {
		try {
			TradeLogger tL = new TradeLogger();
			if (e.isShiftClick()) {
				sendMessage("As of right now shift clicking is disabled for trading.");
				e.setCancelled(true);
				return;
			}
			int slot = e.getRawSlot();
			ItemStack i = e.getCurrentItem();
			TradePlayer oP = getOtherPlayer();
			if (slot > minSlot && slot < maxSlot) {
				if (hasConfirmed()) {
					sendMessage("Un-confirm the trade before trying to make changes!");
					e.setCancelled(true);
				} else {
					if (oP.hasConfirmed()) {
						if ((e.getCursor() != null && e.getCursor().getType() != Material.AIR) || i.getType() != Material.AIR) {
							oP.setConfirmed(false);
							sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
							oP.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
						}
					}
					if (!ConfigLoader.canTrade(e.getCursor())) {
						sendMessage("This item is untradable!");
						e.setCancelled(true);
					}
				}
			} else {
				if (slot > 53) {
					if (!hasConfirmed()) {
						return; // Player inv.
					} else {
						sendMessage("Un-confirm the trade before trying to make changes!");
						e.setCancelled(true);
					}
				}
				if (slot == curSlot) {
					tL.logClicks(getName() + " clicked currency slot.");
					if(player.hasPermission("trademod.currency")) {	
						if(!hasConfirmed()) {
							closeTrade();
							sendMessage("You may now type the amount of currency you would like to remove or add to the trade.");
							sendMessage("I.E. -1000 is a removal of 1000 currency, while 1000 is an addition of 1000 currency.");
							sendMessage("After " + ConfigLoader.getCurTimeout() + " seconds, the trade will resume. You can also re-open the trade window to manually cancel this.");
							modCurrency = true;
							currencyModifyTimer(ConfigLoader.getCurTimeout());
						} else {
							sendMessage("Un-confirm the trade before trying to make changes!");
							e.setCancelled(true);
						}
						if(oP.hasConfirmed()) {
							oP.setConfirmed(false);
							sendMessage("Making a change while the other player is confirmed has automatically un-confirmed the other player.");
							oP.sendMessage("You have automatically un-confirmed due to the other player modifying their offer.");
						}
					} else {
						sendMessage("You do not have the permission to trade currency.");
					}
				}
				if (i != null) {
					if (!(e.isRightClick() && e.isShiftClick())) {
						if (i.getType() == Material.WOOL) {
							if (slot == accSlot) {
								tL.logClicks(getName() + " confirmed offer.");
								if (!oP.hasConfirmed()) {
									// if(e.getCursor() == null) {
									if (i.getDurability() == 5) {
										i.setDurability((short) 3);
										setConfirmed(true);
									} else if (i.getDurability() == 3) {
										setConfirmed(false);
									}
									// }
								} else if (oP.hasConfirmed()) {
									trade.confirm();
									tL.logClicks(getName() + " accepted trade.");
								}
							}
							if (slot == canSlot) {
								cancelTrade(this, "Manually canceled.");
								tL.logClicks(getName() + " clicked cancel.");
							}
						}
					}
				}
				updateScreen(); //Necessary until bukkit implements better syncing.
				e.setCancelled(true);
			}
			
			/*String c = "[Player]: " + getName() + " [slot]: " + e.getRawSlot() + " [iteminslot]: " + i.toString() + " [itemoncursor]: " + e.getCursor().toString() + " [otherplayer]: " + oP.getName();
			if(e.isRightClick())
				c += " [typeofclick]: right";
			else
				c += " [typeofclick]: left";
			tL.logClicks(c);*/
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public Player getPlayer() {
		return player;
	}

	public boolean isModifyingCurrency() {
		return modCurrency;
	}
	
	public void currencyModCancel() {
		modCurrency = false;
	}
	
	public void cancelCurrencyModification() {
		modCurrency = false;
		sendMessage("By re-opening the trade window, you have canceled the request to modify you offered currency.");
		sendMessage("You can click on the emerald to modify currency once more.");
		trm.getServer().getScheduler().cancelTask(w);
	}
	
	public void updateScreen() {
		trm.getServer().getScheduler().scheduleSyncDelayedTask(trm, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				player.updateInventory();
			}
		}, 3);
	}
	public void closeTrade() {
		trm.getServer().getScheduler().scheduleSyncDelayedTask(trm, new Runnable() {
			@Override
			public void run() {
				player.closeInventory();
			}
		});
	}
	
	public void currencyModifyTimer(int seconds) {
		long s = seconds * 20;
		trm.getServer().getScheduler().scheduleSyncDelayedTask(trm, new Runnable() {
			@Override
			public void run() {
				if(modCurrency) {
					modCurrency = false;
					sendMessage("The trade will now be resumed.");
					player.openInventory(trade.getTradeInventory());
				}
			}
		}, s);
	}

	public boolean hasConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean bool) {
		confirmed = bool;
		if (!bool) {
			trade.getTradeInventory().getItem(accSlot).setDurability((short) 5);
			if (trade.requested.equals(this)) {
				trade.requester.sendMessage(getName() + " has un-confirmed their offer.");
				trade.requested.sendMessage("You have un-confirmed your offer.");
			} else {
				trade.requested.sendMessage(getName() + " has un-confirmed their offer.");
				trade.requester.sendMessage("You have un-confirmed your offer.");
			}
		} else {
			trade.getTradeInventory().getItem(accSlot).setDurability((short) 3);
			if (trade.requested.equals(this)) {
				if (!trade.requester.hasConfirmed()) {
					trade.requester.sendMessage(getName() + " has confirmed their offer.");
					trade.requested.sendMessage("You have confirmed your offer.");
				} else {
					trade.confirm();
				}
			} else {
				if (!trade.requested.hasConfirmed()) {
					trade.requested.sendMessage(getName() + " has confirmed their offer.");
					trade.requester.sendMessage("You have confirmed your offer.");
				} else {
					trade.confirm();
				}
			}
		}
	}

	public void timeRequest(int seconds) {
		try {
			long s = seconds * 20;
			w = trm.getServer().getScheduler().scheduleSyncDelayedTask(trm, new Runnable() { //obtain task id so we can cancel it later on if need be.
				@Override
				public void run() {
					cancelRequest(true, "Request timed out.");
				}
			}, s);
		} catch (Exception e) {
			// Request was already accepted, or something stupid happened.
		}
	}

	public void acceptRequest(TradePlayer oP) {
		if (oP != null) {
			if (requesterPlayer != null) {
				requesterPlayer = null;
				oP.requestedPlayer = null;
				inTrade = true;
				oP.inTrade = true;
				trade = new Trade(oP, this, trm);
				oP.trade = trade;
				trade.beginTrade();
			}
		}
	}

	public void setOtherPlayer(TradePlayer who) {
		otherPlayer = who;
	}
	public TradePlayer getOtherPlayer() {
		return otherPlayer;
	}
	public void cancelTrade(TradePlayer who, String reason) {
		reason = ChatColor.RED + reason;
		if (who.equals(this)) {
			if (trade.requested.equals(this)) {
				trade.requester.sendMessage(getName() + " has canceled the trade! Reason: " + reason);
				sendMessage("You have canceled the trade! Reason: " + reason);
			} else {
				trade.requested.sendMessage(getName() + " has canceled the trade! Reason: " + reason);
				sendMessage("You have canceled the trade! Reason: " + reason);
			}
		} else {
			who.sendMessage("You have canceled the trade! Reason: " + reason);
			sendMessage(who.getName() + " has canceled the trade! Reason: " + reason);
		}
		if (!reason.contains("Died")) {
			trade.tradeCanceled();
		}
		trade.requested.closeTrade();
		trade.requester.closeTrade();
		players.remove(trade.requested);
		players.remove(trade.requester);
		players.remove(this);
		trade.requested = null;
		trade.requester = null;
		TradeMod.trades.remove(trade);
	}

	public Trade getTrade() {
		return trade;
	}

	public String getName() {
		return player.getName();
	}

	public void sendMessage(String msg) {
		player.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + msg);
	}

	public boolean isRequested() {
		if (requesterPlayer != null) {
			return true;
		}
		return false;
	}

	public boolean isInTrade() {
		return inTrade;
	}

	public void cancelRequest(boolean bool, String reason) {
		try {
			reason = ChatColor.RED + reason;
			if (bool) {
				if (requestedPlayer != null) {
					sendMessage("Your request to " + requestedPlayer.getName() + " has been declined. Reason: " + reason);
					requestedPlayer.sendMessage(getName() + "'s request has been declined. Reason: " + reason);
				} else {
					requesterPlayer.sendMessage("Your request to " + getName() + " has been declined. Reason: " + reason);
					sendMessage(requesterPlayer.getName() + "'s request has been declined. Reason: " + reason);
				}
			}
			if (requestedPlayer != null) {
				players.remove(requestedPlayer);
			} else {
				players.remove(requesterPlayer);
			}
			players.remove(this);
			requestedPlayer = null;
			requesterPlayer = null;
		} catch (NullPointerException nE) {
		}
	}

	public TradePlayer getRequester() {
		if (isRequested()) {
			return requesterPlayer;
		} else {
			return null;
		}
	}

	public TradePlayer getRequested() {
		return requestedPlayer;
	}

	public static void removePlayer(TradePlayer p) {
		players.remove(p);
		p = null;
	}

	public static TradePlayer getTradePlayer(Player p) {
		return getTradePlayer(p.getName());
	}

	public static TradePlayer getTradePlayer(String name) {
		if (name != null) {
			for (TradePlayer p : players) {
				if (p.player.getName().equalsIgnoreCase(name)) {
					return p;
				}
			}
		}
		return null;
	}

}
