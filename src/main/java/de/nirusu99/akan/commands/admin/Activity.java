package de.nirusu99.akan.commands.admin;

import de.nirusu99.akan.AkanBot;
import de.nirusu99.akan.commands.CommandContext;
import de.nirusu99.akan.commands.Error;
import de.nirusu99.akan.commands.ICommand;
import de.nirusu99.akan.utils.ActivitySetter;
import de.nirusu99.akan.utils.Const;
import org.kohsuke.MetaInfServices;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This {@link ICommand} sets the activity for the bot.
 * The {@link CommandContext#getArgs()} must return 2 or more elements.
 * The different kinds of activities are stored in {@link Activity}.
 *
 * @author Nils Pukropp
 * @since 1.0
 */
@MetaInfServices(ICommand.class)
public final class Activity implements ICommand {
    private static final Pattern PATTERN = Pattern
            .compile("activity (" + Const.ACTIVITY_TYPE_REGEX + ") " + Const.STATUS_REGEX);

    @Override
    public void run(@Nonnull CommandContext ctx) {
        if (AkanBot.userIsOwner(ctx.getAuthor())) {
            List<String> args = ctx.getArgs();
            if (args.size() < 2) {
                ctx.reply(Error.INVALID_ARGUMENTS.toString());
                return;
            }
            StringBuilder status = new StringBuilder();
            args.subList(1, args.size()).forEach(i -> status.append(i).append(" "));
            ctx.getShardManager().setActivity(ActivitySetter
                    .set(args.get(0), status.toString().substring(0, status.length() - 1), ctx.getBot()));
            ctx.reply("Updated activity");
        } else {
            ctx.reply(Error.NOT_OWNER.toString());
        }
    }

    @Override
    public String getName() {
        return "activity";
    }

    @Override
    public String syntax() {
        return "<prefix>status (listening|playing|watching) <message>";
    }

    @Override
    public String gifHelpUrl() {
        return null;
    }

    @Override
    public String toString() {
        return "Sets the activity of the bot";
    }

    @Override
    public Matcher matches(String input) {
        return PATTERN.matcher(input);
    }
}
