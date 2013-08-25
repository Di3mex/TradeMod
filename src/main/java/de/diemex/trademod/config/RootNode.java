package de.diemex.trademod.config;


import de.diemex.trademod.service.ConfigNode;

/**
 * @author Diemex
 */
public enum RootNode implements ConfigNode
{
    /**
     * Timeout in ? before trade will expire?
     */
    TIMEOUT("Timeout", VarType.INTEGER, 10),
    /**
     * Maximum distance between players when they try to trade
     */
    MAX_DISTANCE("Maximum Distance Between Players", VarType.INTEGER, SubType.NATURAL_NUMBER, 10),
    /**
     * If players in creative mode can trade
     */
    CREATIVE_TRADING("Allow Creative Trading", VarType.BOOLEAN, false),
    /**
     * Right clicking a player while in trademode will reopen the menu
     */
    RIGHT_CLICK_REOPEN("Right Click Player Reopen Trade", VarType.BOOLEAN, true),
    /**
     * Right Shift click a player to initiate a trade
     */
    SHIFT_RIGHT_INITIATE("Shift Right Click Player Initiate Trade", VarType.BOOLEAN, true),
    /**
     * the amount of time the player has to type in the amount of currency they would like to add or remove from a trade in seconds.
     */
    CURRENCY_TIMEOUT ("Seconds For Currency Input", VarType.INTEGER, 10),
    /**
     * Output currency in the scoreboard
     */
    SCOREBOARD_CURR ("Show Currency In ScoreBoard", VarType.BOOLEAN, true)
    ;
    private final String path;

    private final VarType type;

    private final SubType subType;

    private final Object defaultValue;


    private RootNode(String path, VarType type, Object def)
    {
        this(path, type, null, def);
    }


    private RootNode(String path, VarType type, SubType subType, Object def)
    {
        this.subType = subType;
        this.path = path;
        this.type = type;
        this.defaultValue = def;
    }


    @Override
    public String getPath()
    {
        return "TradeMod" + '.' + path;
    }


    @Override
    public VarType getVarType()
    {
        return type;
    }


    @Override
    public SubType getSubType()
    {
        return subType;
    }


    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }
}
