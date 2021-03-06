package de.nirusu99.akan.commands.fun.music;

import de.nirusu99.akan.commands.CommandContext;
import de.nirusu99.akan.commands.Error;
import de.nirusu99.akan.commands.ICommand;
import de.nirusu99.akan.core.PlayerManager;
import de.nirusu99.akan.utils.Const;
import de.nirusu99.akan.utils.DiscordUtil;
import net.dv8tion.jda.api.entities.VoiceChannel;

import org.kohsuke.MetaInfServices;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@MetaInfServices(ICommand.class)
public final class Play implements ICommand {
    private static final  Pattern PATTERN = Pattern
        .compile("play (" + Const.LINK + ")");

    @Override
    public void run(CommandContext ctx) {
        if (ctx.getArgs().size() != 1) {
            throw new IllegalArgumentException(Error.
                    INVALID_ARGUMENTS.toString());
        }

        VoiceChannel channel = DiscordUtil.findVoiceChannel(ctx.getSelfMember());

        if (channel != null && !DiscordUtil.areInSameVoice(ctx.getMember(), ctx.getSelfMember())) {
            ctx.reply("You must be in the same voice channel!");
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(ctx, ctx.getArgs().get(0));

        manager.getGuildMusicManager(ctx.getGuild()).getPlayer()
                .setVolume(ctx.getBot().getLogger().getVolume(ctx.getGuild().getIdLong()));

        ctx.getGuild().getAudioManager().openAudioConnection(DiscordUtil.findVoiceChannel(ctx.getMember()));
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String syntax() {
        return "<prefix>play <url>";
    }

    @Override
    public String gifHelpUrl() {
        return null;
    }

    @Override
    public String toString() {
        return "Plays a song from a given source.\n\nSources are:\n"
            + "YouTube\nTwitch\nSoundcloud\nVimo";
    }

    @Override
    public Matcher matches(String input) {
        return PATTERN.matcher(input);
    }
}
