package de.nirusu99.akan.commands.fun.music;

import de.nirusu99.akan.commands.CommandContext;
import de.nirusu99.akan.commands.ICommand;
import de.nirusu99.akan.core.GuildMusicManager;
import de.nirusu99.akan.core.PlayerManager;
import de.nirusu99.akan.utils.DiscordUtil;

import org.kohsuke.MetaInfServices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MetaInfServices(ICommand.class)
public final class Pause implements ICommand {
    private static final Pattern PATTERN = Pattern.compile("pause");

    @Override
    public void run(CommandContext ctx) {
        if (!DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }
        PlayerManager manager = PlayerManager.getInstance();
        GuildMusicManager musicManager = manager.getGuildMusicManager(ctx.getGuild());
        manager.pause(musicManager, !musicManager.getPlayer().isPaused());
    }

    @Override
    public String getName() {
        return "pause";
    }

    @Override
    public String syntax() {
        return "<prefix>pause";
    }

    @Override
    public String gifHelpUrl() {
        return null;
    }

    @Override
    public String toString() {
        return "toggle pause for the music bot";
    }

    @Override
    public Matcher matches(String input) {
        return PATTERN.matcher(input);
    }
}
