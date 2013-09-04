package de.diemex.trademod.service;


/** All known permission nodes. */
public enum PermissionNode
{
    /** Permission to use admin commands like reloading the plugin */
    ADMIN("admin"),
    /** Permission to open a trade request by right clicking the requested player */
    RIGHTCLICK_REQUEST("rightclickrequest"),
    /** Permission to reopen the trade menu by right clicking the other player */
    QUICK_REOPEN("quickreopen"),
    /** Permission to trade with other players */
    CAN_TRADE("cantrade"),
    /** Permission to use currency in trades */
    CURRENCY("currency");

    /** Prefix for all permission nodes. */
    private static final String PREFIX = "trademod.";

    /** Resulting permission node path. */
    private final String node;


    /**
     * Constructor.
     *
     * @param subperm - specific permission path.
     */
    private PermissionNode(String subperm)
    {
        this.node = PREFIX + subperm;
    }


    /**
     * Get the full permission node path.
     *
     * @return Permission node path.
     */
    public String getNode()
    {
        return node;
    }
}
