package de.diemex.trademod;


import de.diemex.trademod.config.RootConfig;
import de.diemex.trademod.config.RootNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.logging.Logger;

public class TradeMod extends JavaPlugin
{
    private RootConfig CFG;
    private PlayerListener pl = new PlayerListener(this);
    private InventoryListener il = new InventoryListener();
    public static Economy economy = null;
    private Logger log;
    public static ArrayList<Trade> trades = new ArrayList<Trade>();


    @Override
    public void onEnable()
    {
        CFG = new RootConfig(this);
        log = this.getLogger();
        log.info("TradeMod is enabled.");
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(pl, this);
        manager.registerEvents(il, this);
        setupEconomy();
    }


    @Override
    public void onDisable()
    {
        log.info("TradeMod has been disabled (?).");
    }


    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }


    public boolean withinRadius(Location loc1, Location loc2, double radius)
    {
        if (radius < 0)
        {
            radius = 10;
        } else if (radius == 0)
        {
            return true;
        }
        int x = (int) (loc2.getX() - loc1.getX());
        int y = (int) (loc2.getY() - loc1.getY());
        int z = (int) (loc2.getZ() - loc1.getZ());
        double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        if (distance <= radius)
        {
            return true;
        } else
        {
            return false;
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("You must be logged in to use trademod's commands");
            return false; // disallow's commands from console.
        }
        if (cmd.getName().equalsIgnoreCase("tm"))
        {
            try
            {
                Player cmdSender = (Player) sender;
                String command = "";
                if (args.length > 0)
                {
                    command = args[0];
                } else
                {
                    cmdSender.sendMessage(ChatColor.AQUA + "/tm CMD [ARG] ... - DESCRIPTION");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm req/request [NAME] - Request a trade with a player. Has restrictions, see /tm help");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm acc/accept - Accepts a trade request.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm dec/decline - Declines a trade request.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm open - If you are in a trade, this command allows you to re-open the trade window.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm can/cancel - If you are not in the trade window, you can cancel the trade with this command.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm con/confirm - If you are not in the trade window, you can confirm the trade with this command.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm addc/addcoin [AMOUNT] - Adds the specified amount of currency to the trade offer.");
                    cmdSender.sendMessage(ChatColor.GOLD + "/tm remc/remcoin [AMOUNT] - Subtracts the specified amount of currency from the trade offer.");
                    cmdSender.sendMessage(ChatColor.BOLD + "" + ChatColor.BLUE + "/tm help - Opens the help menu, contains various pieces of information.");
                }
                if (command != "")
                {
                    if (cmdSender.hasPermission("trademod.cantrade"))
                    {
                        if (command.equalsIgnoreCase("req") || command.equalsIgnoreCase("request"))
                        {
                            Player requestedPlayer = this.getServer().getPlayerExact(args[1]);
                            if (requestedPlayer != null)
                            {
                                int radius = CFG.getInt(RootNode.MAX_DISTANCE);
                                if (!cmdSender.equals(requestedPlayer))
                                {
                                    if (TradePlayer.getTradePlayer(requestedPlayer.getName()) == null)
                                    {
                                        if (TradePlayer.getTradePlayer(cmdSender.getName()) == null)
                                        {
                                            if (withinRadius(requestedPlayer.getLocation(), cmdSender.getLocation(), radius))
                                            {
                                                if (!CFG.getBoolean(RootNode.CREATIVE_TRADING))
                                                {
                                                    if (cmdSender.getGameMode() != GameMode.CREATIVE && requestedPlayer.getGameMode() != GameMode.CREATIVE)
                                                    {
                                                        TradePlayer rP = new TradePlayer(this, requestedPlayer);
                                                        TradePlayer p = new TradePlayer(this, cmdSender);
                                                        if (p.requestTrade(rP))
                                                        {
                                                            rP.sendMessage(p.getName() + " would like to trade with you, type /tm acc to accept the request, or type /tm dec to decline it.");
                                                            if (requestedPlayer.hasPermission("trademod.rightclickrequest") && CFG.getBoolean(RootNode.SHIFT_RIGHT_INITIATE))
                                                                rP.sendMessage("You can also sneak and right click, while unarmed, the other player to accept the request.");
                                                            p.sendMessage("You have requested " + rP.getName() + " to trade with you. The request will automatically cancel in " + CFG.getInt(RootNode.TIMEOUT) + " seconds");
                                                        }
                                                    } else
                                                    {
                                                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
                                                        requestedPlayer.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade.");
                                                    }
                                                } else
                                                {
                                                    TradePlayer rP = new TradePlayer(this, requestedPlayer);
                                                    TradePlayer p = new TradePlayer(this, cmdSender);
                                                    if (p.requestTrade(rP))
                                                    {
                                                        rP.sendMessage(p.getName() + " would like to trade with you, type /tm acc to accept the request, or type /tm dec to decline it.");
                                                        if (requestedPlayer.hasPermission("trademod.rightclickrequest") && CFG.getBoolean(RootNode.SHIFT_RIGHT_INITIATE))
                                                            rP.sendMessage("You can also sneak and right click, while unarmed, the other player to accept the request.");
                                                        p.sendMessage("You have requested " + rP.getName() + " to trade with you. The request will automatically cancel in " + CFG.getInt(RootNode.TIMEOUT) + " seconds");
                                                    }
                                                }
                                            } else
                                            {
                                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You need to be within " + radius + " blocks of that player!");
                                            }
                                        } else
                                        {
                                            cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You are currently requested or in a trade.");
                                        }
                                    } else
                                    {
                                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + requestedPlayer.getName() + " is currently requested or in a trade.");
                                    }
                                } else
                                {
                                    cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You cannot trade yourself!");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "That player is offline.");
                            }
                        } else if (command.equalsIgnoreCase("acc") || command.equalsIgnoreCase("accept"))
                        {
                            TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                            if (rP != null)
                            {
                                if (rP.isRequested())
                                {
                                    TradePlayer oP = rP.getRequester();
                                    rP.sendMessage("You have accepted " + oP.getName() + "'s request to trade.");
                                    oP.sendMessage(rP.getName() + " has accepted your request to trade.");
                                    rP.acceptRequest(oP);
                                } else
                                {
                                    rP.sendMessage("You have not been requested to trade with anyone!");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You have not been requested to trade with anyone!");
                            }
                        } else if (command.equalsIgnoreCase("dec") || command.equalsIgnoreCase("decline"))
                        {
                            TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                            if (rP != null)
                            {
                                if (rP.isRequested())
                                {
                                    TradePlayer oP = rP.getRequester();
                                    rP.sendMessage("You have declined " + oP.getName() + "'s request to trade.");
                                    oP.sendMessage(rP.getName() + " has declined your request to trade.");
                                    oP.cancelRequest(false, "Manually declined.");
                                } else
                                {
                                    rP.sendMessage("You have not been requested to trade with anyone!");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You have not been requested to trade with anyone!");
                            }
                        } else if (command.equalsIgnoreCase("open"))
                        {
                            TradePlayer p = TradePlayer.getTradePlayer(cmdSender);
                            if (p != null)
                            {
                                if (p.isInTrade())
                                {
                                    if (p.tradeInv != null)
                                    {
                                        // p.openTrade(p.getTrade().getTradeInventory());
                                        p.sendMessage("Re-opening trade screen.");
                                        p.openTrade();
                                    }
                                }
                            }
                        } else if (command.equalsIgnoreCase("can") || command.equalsIgnoreCase("cancel"))
                        {
                            TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                            if (rP != null)
                            {
                                if (rP.isInTrade())
                                {
                                    rP.cancelTrade(rP, "Manually canceled.");
                                } else
                                {
                                    cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You are not currently in a trade!");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You are not currently in a trade!");
                            }
                        } else if (command.equalsIgnoreCase("con") || command.equalsIgnoreCase("confirm"))
                        {
                            TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                            if (rP != null)
                            {
                                if (rP.isInTrade())
                                {
                                    if (!rP.hasConfirmed())
                                    {
                                        rP.setConfirmed(true);
                                    } else
                                    {
                                        rP.setConfirmed(false);
                                    }
                                } else
                                {
                                    cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You are not currently in a trade!");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You are not currently in a trade!");
                            }
                        } else if (command.equalsIgnoreCase("addc") || command.equalsIgnoreCase("addcoin"))
                        {
                            if (cmdSender.hasPermission("trademod.currency"))
                            {
                                TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                                int amount = 0;
                                if (args.length > 1)
                                {
                                    amount = Integer.parseInt(args[1]);
                                    if (rP != null)
                                    {
                                        if (rP.isInTrade())
                                        {
                                            if (!rP.hasConfirmed())
                                            {
                                                if (amount > 0)
                                                {
                                                    if (economy != null)
                                                    {
                                                        Trade t = rP.getTrade();
                                                        double bal = economy.getBalance(rP.getName());
                                                        if (bal > 0)
                                                        {
                                                            if (!(amount > bal))
                                                            {
                                                                t.addCurrency(rP, amount);
                                                            } else
                                                            {
                                                                rP.sendMessage("You do not have " + amount + " " + economy.currencyNameSingular() + "!");
                                                            }
                                                        } else
                                                        {
                                                            rP.sendMessage("You do not have any " + economy.currencyNameSingular() + "!");
                                                        }
                                                    } else
                                                    {
                                                        rP.sendMessage("This server does not support currency in trades!");
                                                    }
                                                } else
                                                {
                                                    rP.sendMessage(args[1] + " is an incorrect amount! It must be a number higher than 0.");
                                                }
                                            } else
                                            {
                                                rP.sendMessage("You must un-confirm your offer first. (/tm con)");
                                            }
                                        } else
                                        {
                                            rP.sendMessage("You need to be in a trade first!");
                                        }
                                    } else
                                    {
                                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You need to be requested or in a trade.");
                                    }
                                } else
                                {
                                    cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You need to follow this syntax: /tm addc AMOUNT");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.RED + "You do not have permission!");
                            }
                        } else if (command.equalsIgnoreCase("remc") || command.equalsIgnoreCase("remcoin"))
                        {
                            if (cmdSender.hasPermission("trademod.currency"))
                            {
                                TradePlayer rP = TradePlayer.getTradePlayer(cmdSender);
                                int amount = 0;
                                if (args.length > 1)
                                {
                                    amount = Integer.parseInt(args[1]);
                                    if (rP != null)
                                    {
                                        if (rP.isInTrade())
                                        {
                                            if (!rP.hasConfirmed())
                                            {
                                                if (amount > 0)
                                                {
                                                    if (economy != null)
                                                    {
                                                        Trade t = rP.getTrade();
                                                        if (economy.getBalance(rP.getName()) > 0)
                                                        {
                                                            t.remCurrency(rP, amount);
                                                        }
                                                    } else
                                                    {
                                                        rP.sendMessage("This server does not support currency in trades!");
                                                    }
                                                } else
                                                {
                                                    rP.sendMessage(args[1] + " is an incorrect amount! It must be a number higher than 0.");
                                                }
                                            } else
                                            {
                                                rP.sendMessage("You must un-confirm your offer first. (/tm con)");
                                            }
                                        } else
                                        {
                                            rP.sendMessage("You need to be in a trade first!");
                                        }
                                    } else
                                    {
                                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You need to be requested or in a trade.");
                                    }
                                } else
                                {
                                    cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "You need to follow this syntax: /tm remc AMOUNT");
                                }
                            } else
                            {
                                cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.RED + "You do not have permission!");
                            }
                        }
                    } else
                    {
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.RED + "You do not have permission to trade!");
                    }
                    if (command.equalsIgnoreCase("help"))
                    {
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Text Tutorial: http://dev.bukkit.org/server-mods/trademod/pages/help/");
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Video Tutorial: http://www.youtube.com/watch?v=YEs5tqUhuKk&feature=plcp");
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Maximum trade/request distance: " + CFG.getInt(RootNode.MAX_DISTANCE) + " blocks.");
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "Request time-out length: " + CFG.getInt(RootNode.TIMEOUT) + " seconds.");
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "I can trade, and use commands: " + Boolean.toString(cmdSender.hasPermission("trademod.cantrade")));
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "I can use currency commands (addcoin, remcoin): " + Boolean.toString(cmdSender.hasPermission("trademod.currency")));
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "In a trade I can re-open the window by right clicking the other person: " + Boolean.toString(cmdSender.hasPermission("trademod.quickreopen")));
                        cmdSender.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD + "I can request another person to trade by right-clicking them while sneaking: " + Boolean.toString(cmdSender.hasPermission("trademod.rightclickrequest")));
                    }
                    //TradeLogger tL = new TradeLogger();
                    //tL.logCmds("[player]: " + cmdSender.getName() + " [commandarg]: " + command);
                }
            } catch (Exception e)
            {
            }
            return true;
        }
        return false;
    }

    public RootConfig getCFG ()
    {
        return CFG;
    }
}
