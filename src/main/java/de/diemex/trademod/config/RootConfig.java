package de.diemex.trademod.config;


import de.diemex.trademod.service.ConfigNode;
import de.diemex.trademod.service.ModularConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

/**
 * @author Diemex
 */
public class RootConfig extends ModularConfig
{
    public RootConfig(Plugin plugin)
    {
        super(plugin);
    }


    @Override
    public void load()
    {
        loadDefaults(plugin.getConfig());
        plugin.saveConfig();
        reload();
    }


    @Override
    public void close()
    {
        plugin.reloadConfig();
        plugin.saveConfig();
    }


    @Override
    public void save()
    {
        plugin.saveConfig();
    }


    @Override
    public void set(String path, Object value)
    {
        final ConfigurationSection config = plugin.getConfig();
        config.set(path, value);
        plugin.saveConfig();
    }


    @Override
    public void reload()
    {
        plugin.reloadConfig();
        loadSettings(plugin.getConfig());
        boundsCheck();
    }


    @Override
    public void loadSettings(ConfigurationSection config)
    {
        for (final RootNode node : RootNode.values())
        {
            updateOption(node, config);
        }
    }


    @Override
    public void loadDefaults(ConfigurationSection config)
    {
        for (RootNode node : RootNode.values())
        {
            if (!config.contains(node.getPath()))
            {
                config.set(node.getPath(), node.getDefaultValue());
            }
        }
    }


    @Override
    public void boundsCheck()
    {
        for (RootNode node : RootNode.values())
        {
            //Only basic verification for now
            if (node.getSubType() == ConfigNode.SubType.NATURAL_NUMBER && node.getVarType() == ConfigNode.VarType.INTEGER)
            {
                int oldVal = getInt(node);
                int newVal = validateCustomBounds(node, 0, 0, oldVal);
                if (newVal != oldVal) // we adjusted the value
                    set(node, newVal);
            }
        }
    }


    /**
     * Validates a configOption with custom bounds
     *
     * @param node   the configNode
     * @param minVal the minimum value the config is allowed to have
     * @param maxVal the maximum value for the config, if == minVal then it doesn't get checked
     * @param value  - Integer to validate
     *
     * @return a Response containing either the original value or adjusted if out of bounds and the Status
     */
    private int validateCustomBounds(ConfigNode node, int minVal, int maxVal, Integer value)
    {
        if (value < minVal)
        {
            if (plugin.getLogger() != null) //testing
                plugin.getLogger().warning("TradeMod" + " Value for " + node.getPath() + " cannot be smaller than " + minVal);
            value = minVal;
        } else if (minVal < maxVal && value > maxVal)
        {
            if (plugin.getLogger() != null) //testing
                plugin.getLogger().warning("TradeMod" + " Value for " + node.getPath() + " cannot be greater than " + maxVal);
            value = maxVal;
        }
        return value;
    }
}
