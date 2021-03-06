package de.nirusu99.akan.commands.admin;

import de.nirusu99.akan.AkanBot;
import de.nirusu99.akan.commands.CommandContext;
import de.nirusu99.akan.commands.Error;
import de.nirusu99.akan.commands.ICommand;
import de.nirusu99.akan.core.GuildManager;
import de.nirusu99.akan.utils.Const;
import net.dv8tion.jda.api.Permission;

import org.kohsuke.MetaInfServices;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link ICommand} sets the bot prefix. The bot keeps the new prefix because of {@link AkanBot#setPrefix(String)}
 * that uses the {@link de.nirusu99.akan.core.Config} to store the prefix.
 *
 * @author Nils Pukropp
 * @since 1.0
 */
@MetaInfServices(ICommand.class)
public final class Prefix implements ICommand {
    private static final Pattern PATTERN = Pattern.compile("prefix " + Const.PREFIX_REGEX);

    @Override
    public void run(@Nonnull CommandContext ctx) {
        List<String> args = ctx.getArgs();
        if (args.size() != 1) {
            ctx.reply(Error.INVALID_ARGUMENTS.toString());
            return;
        }
        if (ctx.getMember().hasPermission(Permission.ADMINISTRATOR) || AkanBot.userIsOwner(ctx.getAuthor())) {
            String newPrefix = args.get(0);
            GuildManager gm = GuildManager.getManager(ctx.getGuild().getIdLong(), ctx.getBot());
            if (!gm.setPrefix(newPrefix)) {
                ctx.reply("Wups something went wrong");
                return;
            }
            ctx.reply("Prefix was set to " + newPrefix + " <:remV:639621688887083018>");
        } else {
            ctx.reply("Only admins can change the prefix!");
        }
    }

    @Override
    public String getName() {
        return "prefix";
    }

    @Override
    public String syntax() {
        return "<prefix>prefix <new prefix>";
    }

    @Override
    public String toString() {
        return "Changes the prefix of the bot for this server.";
    }

    @Override
    public String gifHelpUrl() {
        return null;
    }

    @Override
    public Matcher matches(String input) {
        return PATTERN.matcher(input);
    }
}
