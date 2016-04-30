package org.jwave.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.jwave.controller.player.AudioSystem;
import org.jwave.controller.player.DynamicPlayer;
import org.jwave.controller.player.PlaylistManager;
import org.jwave.model.player.Song;

public final class UsePlayer {

    private static final String PATH = "/home/canta/Music";
    
    private UsePlayer() { }
    
    public static void main(final String... args) throws FileNotFoundException, IOException, IllegalArgumentException, ClassNotFoundException {
        final DynamicPlayer player = AudioSystem.getAudioSystem().getDynamicPlayer();
        final PlaylistManager manager = AudioSystem.getAudioSystem().getPlaylistManager();
        manager.openDir(PATH, true);
        manager.savePlaylistToFile("playlistProva", PATH);
        manager.loadPlaylist(PATH + "/playlistProva.jwo");
        manager.getPlayingQueue().printPlaylist();
//        player.setPlayer(new SongImpl(PATH + "/03. The Gift Of Music.mp3"));
        player.setPlayer(manager.getPlayingQueue().selectSong(4));
        player.play();
        System.out.println("current loaded: " + manager.getCurrentLoaded().get().getName());
        //retrieving a value from metadata
        System.out.println(manager.getPlayingQueue().getCurrentSelected().get().getMetaData().getAlbum());
    }

}
