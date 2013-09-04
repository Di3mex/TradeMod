package de.diemex.trademod;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/** @author Diemex */
public enum Message
{
    /*
     * CURRENCY ERRORS
     */
    /** Args: currency amount (double), currency name (string) */
    ERR_NOT_ENOUGH_CURRENCY("You do not have %.2f %s!"),
    /** Args: currency name (string) */
    ERR_NO_CURRENCY("You do not have any %s!"),
    ERR_NO_ECONOMY_ACTIVE("This server does not support currency in trades!"),
    /** Args: command line arg (string) */
    ERR_CURRENCY_INVALID_NEG("%s is an incorrect amount! It must be a number higher than 0."),
    ERR_CURRENCY_WRONG_SYNTAX("You need to follow this syntax: /tm addc AMOUNT"),
    ERR_CURRENCY_INVALID_INPUT("You must enter a number. Negative numbers indicate a removal of currency from the offer",
            "while positive numbers indicate an addition to your offer."),
    /*
     * CURRENCY MESSAGES
     */
    /** Args: currency amount (double), currency name (string) */
    MSG_CURRENCY_ALL_IN("You have added your entire balance of %.2f %s to the trade!"),
    /** Args: player name (string), currency amount (double), currency name (string) */
    MSG_CURRENCY_ALL_IN_OTHER("%s has added their entire balance of %.2f %s to the trade!"),
    /** Args: currency amount (double), currency name (string) */
    MSG_CURRENCY_ADD("You have added %.2f %s to the trade!"),
    /** Args: player name (string), currency amount (double), currency name (string) */
    MSG_CURRENCY_ADD_OTHER("%s has added %.2f %s to the trade!"),
    /** Args: currency amount (double), currency name (string), currency amount left (double) */
    MSG_CURRENCY_REMOVE("You have removed %.2f %s from the trade, current amount left: %.2f"),
    /** Args: player name (string), currency amount (double), currency name (string), currency amount left (double) */
    MSG_CURRENCY_REMOVE_OTHER("%s has removed %.2f %s from the trade, current amount left: %.2f"),
    MSG_CURRENCY_ALL_OUT("You have removed all currency from the offer."),
    /** Args: player name (string) */
    MSG_CURRENCY_ALL_OUT_OTHER("%s has removed all currency from the offer."),

    MSG_CURRENCY_MODIFY_PART1("You may now type the amount of currency you would like to remove or add to the trade.",
            "I.E. -1000 is a removal of 1000 currency, while 1000 is an addition of 1000 currency."),
    /** Args: delay in seconds (int), */
    MSG_CURRENCY_MODIFY_PART2("After %u seconds, the trade will resume. You can also re-open the trade window to manually cancel this."),
    MSG_CURRENCY_CANCELED_REOPEN("By re-opening the trade window, you have canceled the request to modify you offered currency.", "You can click on the emerald to modify currency once more."),
    /** Args: payed to player (string), currency amount (double), currency name (string) */
    MSG_CURRENCY_SUCCES("You have payed %s %.2f %s!"),
    /** Args: currency amount (double), currency name (string), received from player (string) */
    MSG_CURRENCY_SUCCESS_OTHER("You have received %.2f %s from %s!"),
    /*
     * CONFIRM UNCONFIRM MESSAGES
     */
    ERR_UNCONFIRM_BEFORE_EDIT("Un-confirm the trade before trying to make changes!"),
    MSG_UNCONFIRM_AFTER_EDIT("Making a change while the other player is confirmed has automatically un-confirmed the other player."),
    MSG_UNCONFIRM_AFTER_EDIT_OTHER("You have automatically un-confirmed due to the other player modifying their offer."),
    MSG_UNCONFIRM_CLOSE_INV("Closing the trade has auto unconfirmed your trade, use /tm open to return to the screen and re-confirm."),

    MSG_UNCONFIRM("You have un-confirmed your offer."),
    /** Args: player name (string) */
    MSG_UNCONFIRM_OTHER("%s has un-confirmed their offer."),
    MSG_CONFIRM("You have confirmed your offer."),
    /** Args: player name (string) */
    MSG_CONFIRM_OTHER("%s has confirmed their offer."),
    /*
     * GENERAL TRADE MESSAGES
     */
    /** Args: player name (string) */
    MSG_TRADE_REQUEST("%s would like to trade with you, type /tm acc to accept the request, or type /tm dec to decline it."),
    MSG_TRADE_REQUEST_RC_TIP("You can also sneak and right click, while unarmed, the other player to accept the request."),
    MSG_TRADE_REQUEST_OTHER("You have requested %s to trade with you. The request will automatically cancel in %u seconds"),
    MSG_TRADE_TUT("You have the top portion of the screen."),
    MSG_TRADE_TUT_OTHER("You have the bottom portion of the screen."),
    /** Args: reason phrase (string) */
    MSG_TRADE_CANCEL("You have canceled the trade! Reason: %s"),
    /** Args: player name (string), reason phrase (string) */
    MSG_TRADE_CANCEL_OTHER("%s has canceled the trade! Reason: %s"),
    /** Args: player name (string) */
    MSG_TRADE_ACCEPT("You have accepted %s's request to trade."),
    /** Args: player name (string) */
    MSG_TRADE_ACCEPT_OTHER("%s has accepted your request to trade."),
    /** Args: player name (string), reason phrase (string) */
    MSG_TRADE_DECLINE("Your request to %s has been declined. Reason: %s"),
    /** Args: player name (string), reason phrase (string) */
    MSG_TRADE_DECLINE_OTHER("%s's request has been declined. Reason: %s"),
    MSG_TRADE_SUCCESS("The trade was successful. You may now trade others."),

    MSG_RESUME_TRADE("The trade will now be resumed."),
    MSG_REOPEN_SCREEN("Re-opening trade screen."),
    /*
     * ERROR MESSAGES
     */
    ERR_CREATIVE_TRADING_DISABLED("Creative trading is currently disabled. Currently, one of the players involved is in the Creative game mode. Switch to Survival to commence the trade."),
    ERR_UNTRADABLE("This item is untradable!"),

    ERR_NO_TRADE("You are not currently in a trade!"),
    /** Args: range in blocks (int) */
    ERR_RADIUS_EXCEED("You need to be within %u blocks of that player!"),
    /** Args: player name (string) */
    ERR_IN_TRADE("%s is currently in a trade or requested!"),
    ERR_TRADE_WITH_SELF("You cannot trade with yourself!"),
    ERR_OTHER_OFFLINE("That player is offline"),
    ERR_NO_PERM_CURRENCY("You do not have the permission to trade currency."),
    ERR_NO_PERM_TRADE("You do not have permission to trade!"),

    ERR_NOT_ENOUGH_SPACE("You have too many items in your current inventory to confirm the trade. Please remove some items before attempting to trade the current inventory."),
    /** Args: player name (string) */
    ERR_NOT_ENOUGH_SPACE_OTHER("%s had too many items in their inventory to complete the trade."),

    ERR_SHIFT_CLICK_DISABLED("As of right now shift clicking is disabled for trading."),;
    private final String[] msgText;


    private Message(String... message)
    {
        msgText = message;
    }


    /**
     * Send a message to a player make sure you don't mess the parameters up
     *
     * @param player player to send the message to
     * @param args   blanks to fill in (if any)
     */
    public void send(Player player, Object... args)
    {
        for (String text : msgText)
            player.sendMessage(ChatColor.GREEN + "[TM] " + ChatColor.GOLD +
                    String.format(text, args));
    }
}
