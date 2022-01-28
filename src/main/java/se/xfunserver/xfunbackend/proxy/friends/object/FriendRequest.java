package se.xfunserver.xfunbackend.proxy.friends.object;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import se.xfunserver.xfunbackend.proxy.Core;
import se.xfunserver.xfunbackend.proxy.friends.FriendManager;
import se.xfunserver.xfunbackend.proxy.user.object.ProxyUser;

public class FriendRequest {

    private final FriendManager friendManager;

    @Getter
    private final ProxyUser senderUser;
    @Getter
    private final ProxyUser targetUser;

    public FriendRequest(ProxyUser senderUser, ProxyUser targetUser) {
        this.friendManager = Core.getCore().getFriendManager();
        this.senderUser = senderUser;
        this.targetUser = targetUser;
    }

    public void onAccept() {
        this.targetUser.sendMessage("Du är nu vän med &b" + this.senderUser.getProxiedPlayer().getName());
        this.senderUser.sendMessage(this.targetUser.getProxiedPlayer().getName() + " accepterade din förfrågan.");

        this.friendManager.addFriend(this.senderUser, this.targetUser);
    }

    public void onDeny() {

    }

    public boolean send() {
        if (this.friendManager.getFriendRequests().containsKey(this.senderUser))
            return false;

        this.friendManager.addFriendRequest(this);
        this.targetUser.getProxiedPlayer().sendMessage(getMessage());
        this.senderUser.sendMessage("Du har skickat en vänförfrågan till " + targetUser.getProxiedPlayer().getName());
        return true;
    }

    public TextComponent getMessage() {
        TextComponent textComponent = new TextComponent(" \n");
        textComponent.addExtra(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "NY VÄNFÖRFRÅGAN " + ChatColor.GRAY + "-" + " " + ChatColor.AQUA + this.senderUser.getProxiedPlayer().getName());

        TextComponent acceptText = new TextComponent("Acceptera");
        acceptText.setColor(ChatColor.GREEN);
        acceptText.setBold(true);
        acceptText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/friend accept " + this.targetUser.getProxiedPlayer().getName()));

        TextComponent declineText = new TextComponent("Neka");
        acceptText.setColor(ChatColor.RED);
        acceptText.setBold(true);
        acceptText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/friend deny " + this.targetUser.getProxiedPlayer().getName()));

        textComponent.addExtra("\n");
        textComponent.addExtra(acceptText);
        textComponent.addExtra("         ");
        textComponent.addExtra(declineText);
        textComponent.addExtra("\n");

        return textComponent;
    }
}
