package fr.pmk.lucksecure.command;

import net.md_5.bungee.api.chat.BaseComponent;

public interface ICommandAdapter<S, P extends S> {
    public boolean isSenderInstanceOfPlayer(S sender);
    public void sendMessageToSender(S sender, BaseComponent[] msg);
    public P getPlayerFromSender(S sender);
    public P getServerPlayer(String name);
    public String getServerName();
}
