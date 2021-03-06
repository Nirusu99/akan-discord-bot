package de.nirusu99.akan.core;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.nirusu99.akan.commands.CommandContext;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles all the music for the bot.
 *
 * @author Nils Pukropp
 * @version 1.2
 */
public class PlayerManager {
    private static PlayerManager instance;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public synchronized GuildMusicManager getGuildMusicManager(@Nonnull final Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }


    public void loadAndPlay(@Nonnull final CommandContext ctx, @Nonnull  String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(ctx.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(@Nonnull AudioTrack track) {
                play(musicManager, track);
                ctx.reply("Loaded: " + track.getInfo().author
                                + "-" + track.getInfo().title);
            }

            @Override
            public void playlistLoaded(@Nonnull final AudioPlaylist playlist) {
                playlist.getTracks().forEach(track -> play(musicManager, track));
                ctx.reply("Loaded: Playlist: " + playlist.getName());
            }

            @Override
            public void noMatches() {
                ctx.reply("Couldn't find: " + trackUrl);
            }

            @Override
            public void loadFailed(@Nonnull FriendlyException exception) {
                ctx.getBot().printInfo(exception.getMessage());
                ctx.reply("Well done, you destroyed the bot, volvo pls fix...");
            }
        });
    }

    private void play(@Nonnull final GuildMusicManager musicManager, @Nonnull final AudioTrack track) {
        musicManager.getScheduler().queue(track);
    }

    public void pause(@Nonnull final GuildMusicManager musicManager, final boolean pause) {
        musicManager.getPlayer().setPaused(pause);
    }

    public void destroy(@Nonnull final Guild guild) {
        musicManagers.get(guild.getIdLong()).getPlayer().destroy();
        musicManagers.remove(guild.getIdLong());
    }

    public void next(@Nonnull final GuildMusicManager musicManager) {
        musicManager.getScheduler().nextTrack();
    }

    public void shuffle(@Nonnull final GuildMusicManager musicManager) {
        musicManager.getScheduler().shuffle();

    }

    public boolean repeat(@Nonnull final GuildMusicManager musicManager) {
        return musicManager.getScheduler().setRepeat();
    }

    public AudioTrack getPlaying(@Nonnull final GuildMusicManager musicManager) {
        return musicManager.getPlayer().getPlayingTrack();
    }

    public static synchronized PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }

        return instance;
    }
}
