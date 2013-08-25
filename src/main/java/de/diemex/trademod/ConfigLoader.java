package de.diemex.trademod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ConfigLoader {
	private static File config = new File("plugins/TradeMod/preferences.txt");

	public static int getMaxDistance() {
		try {
			if (!config.exists()) {
				return 10;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("distance =")) {
						String propSplit[] = line.split("= ");
						return Integer.parseInt(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return 10;
	}

	public static int getCurTimeout() {
		try {
			if (!config.exists()) {
				return 10;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("currencyTimeout =")) {
						String propSplit[] = line.split("= ");
						return Integer.parseInt(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return 10;
	}
	
	public static int getTimeout() {
		try {
			if (!config.exists()) {
				return 10;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("timeout =")) {
						String propSplit[] = line.split("= ");
						return Integer.parseInt(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return 10;
	}

	public static boolean fullTradeReq() {
		try {
			if (!config.exists()) {
				return false;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("trade-with-full-inv =")) {
						String propSplit[] = line.split("= ");
						return Boolean.parseBoolean(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return false;
	}

	public static boolean creativeToSurv() {
		try {
			if (!config.exists()) {
				return false;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("creative-trading =")) {
						String propSplit[] = line.split("= ");
						return Boolean.parseBoolean(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return false;
	}

	public static boolean canTrade(ItemStack i) {
		try {
			File f = new File("plugins/TradeMod/blacklist.txt");
			if (!f.exists()) {
				return true;
			}
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (i != null && i.getType() != Material.AIR) {
						if (line.contains(i.getTypeId() + "")) {
							if (line.contains(":")) {
								String split = line.split(":")[1];
								int a = Integer.parseInt(split);
								if (a == i.getDurability()) {
									return false;
								}
							} else {
								return false;
							}
						}
					}
				}
			}
			return true;
		} catch (IOException x) {
			x.printStackTrace();
		}
		return true;
	}
	
	public static boolean getReopenEnabled() {
		try {
			if (!config.exists()) {
				return false;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("reopen-enabled =")) {
						String propSplit[] = line.split("= ");
						return Boolean.parseBoolean(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return false;
	}
	
	public static boolean getRequestEnabled() {
		try {
			if (!config.exists()) {
				return true;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("rcrequest-enabled =")) {
						String propSplit[] = line.split("= ");
						return Boolean.parseBoolean(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return true;
	}

	public static boolean scoreboardEnabled() {
		try {
			if (!config.exists()) {
				return true;
			}
			BufferedReader reader = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					if (line.contains("scoreboard-enabled =")) {
						String propSplit[] = line.split("= ");
						return Boolean.parseBoolean(propSplit[1]);
					}
				}
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
		return true;
	}
}
