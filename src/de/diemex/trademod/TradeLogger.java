package de.diemex.trademod;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class TradeLogger {

	public void logTrade(Trade t) {
		try {
			File a = new File("plugins/TradeMod/tradelogs.txt");
			FileWriter f = new FileWriter(a, true);
			if (!a.exists()) {
				a.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(f);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			out.write("[" + t.requested.getName().toUpperCase() + " + " + t.requester.getName().toUpperCase() + "]");
			out.newLine();
			out.write("<" + t.requested.getName().toUpperCase() + "'s OFFER>");
			out.newLine();
			for (ItemStack i : t.requestedItems) {
				if (i.getEnchantments().isEmpty()) {
					out.write(i.toString().split("ItemStack")[1].toLowerCase());
					out.newLine();
				} else {
					String enchantments = "";
					for (Enchantment e : i.getEnchantments().keySet()) {
						enchantments += e.getName() + "{" + i.getEnchantmentLevel(e) + "} ";
					}
					out.write(i.toString().split("ItemStack")[1].toLowerCase() + " Enchs: " + enchantments.toLowerCase());
					out.newLine();
				}
			}
			out.write("Payed Currency: " + t.requestedCur);
			out.newLine();
			out.write("<END " + t.requested.getName().toUpperCase() + "'s OFFER>");
			out.newLine();
			out.write("<" + t.requester.getName().toUpperCase() + "'s OFFER>");
			out.newLine();
			for (ItemStack i : t.requesterItems) {
				if (i.getEnchantments().isEmpty()) {
					out.write(i.toString().split("ItemStack")[1].toLowerCase());
					out.newLine();
				} else {
					String enchantments = "";
					for (Enchantment e : i.getEnchantments().keySet()) {
						enchantments += e.getName() + "{" + i.getEnchantmentLevel(e) + "} ";
					}
					out.write(i.toString().split("ItemStack")[1].toLowerCase() + " Enchs: " + enchantments.toLowerCase());
					out.newLine();
				}
			}
			out.write("Payed Currency: " + t.requesterCur);
			out.newLine();
			out.write("<END " + t.requester.getName().toUpperCase() + "'s OFFER>");
			out.newLine();
			out.write("TIME ENDED: " + dateFormat.format(cal.getTime()));
			out.newLine();
			out.write("[END TRADELOG]");
			out.newLine();
			out.newLine();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void logClicks(String msg) {
		try {
			File a = new File("plugins/TradeMod/clicks.txt");
			FileWriter f = new FileWriter(a, true);
			if (!a.exists()) {
				a.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(f);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			out.write(dateFormat.format(cal.getTime()) + ": " + msg);
			out.newLine();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void logCmds(String msg) {
		try {
			File a = new File("plugins/TradeMod/commands.txt");
			FileWriter f = new FileWriter(a, true);
			if (!a.exists()) {
				a.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(f);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			out.write(dateFormat.format(cal.getTime()) + ": " + msg);
			out.newLine();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}