package org.jwave.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.jwave.controller.player.ClockAgent;
import org.jwave.controller.player.PlaylistController;
import org.jwave.model.editor.DynamicEditorPlayerImpl;
import org.jwave.model.player.DynamicPlayer;
import org.jwave.model.player.DynamicPlayerImpl;
import org.jwave.model.player.Playlist;
import org.jwave.model.player.PlaylistManager;
import org.jwave.model.player.PlaylistManagerImpl;
import org.jwave.model.player.Song;
import org.jwave.view.PlayerUIObserver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class Controller implements PlayerUIObserver {

    private final DynamicPlayer player;
    private final DynamicPlayer editorPlayer;
    private final PlaylistManager manager;
    private final ClockAgent agent;

    private final ObservableList<Playlist> playlists;

    Controller() {

        try {
            PlaylistController.checkDefaultDir();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.player = new DynamicPlayerImpl();
        this.editorPlayer = new DynamicEditorPlayerImpl(new DynamicPlayerImpl());
        this.manager = new PlaylistManagerImpl(PlaylistController.loadDefaultPlaylist());
        this.agent = new ClockAgent(player, player, manager, "agent");
        this.agent.startClockAgent();
        try {
            manager.setAvailablePlaylists(PlaylistController.reloadAvailablePlaylists());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        manager.setQueue(manager.getDefaultPlaylist());

        this.playlists = FXCollections.observableArrayList(this.manager.getAvailablePlaylists());

    }

    @Override
    public void loadSong(File song) {
        System.out.println("load " + song.getPath() + "  " + song);
        this.manager.addAudioFile(song);
        if(player.isEmpty()){
            manager.setQueue(manager.getDefaultPlaylist());
            this.player.setPlayer(manager.getDefaultPlaylist().getSong(0));
        }

        System.out.print("PLAYING QUEUE: ");
        for (int i = 0; i < manager.getPlayingQueue().getDimension(); i++) {
            System.out.print(manager.getPlayingQueue().getSong(i).getName()+"  ");
        }
        System.out.println();
    }

    @Override
    public void loadSong(Path path) {

    }

    @Override
    public void play() {
        if (this.player.isPlaying()) {
            this.player.pause();
        } else {
            this.player.play();
        }
    }

    @Override
    public void stop() {
        this.player.stop();
    }

    @Override
    public void next() {
        final boolean wasPlaying = this.player.isPlaying();
        try {
            final Optional<Song> nextSong = this.manager.next();
            if (nextSong.isPresent()) {
                this.player.setPlayer(nextSong.get());
                if (wasPlaying) {
                    this.player.play();
                }
            }
        } catch (IllegalStateException e) {
            System.out.println("You must add at last one song in the playlist");
        }
    }

    @Override
    public void previous() {
        final boolean wasPlaying = this.player.isPlaying();
        try {
            final Optional<Song> prevSong = this.manager.prev();
            if (prevSong.isPresent()) {
                this.player.setPlayer(prevSong.get());
                if (wasPlaying) {
                    this.player.play();
                }
            }
        } catch (IllegalStateException e) {
            System.out.println("You must add at last one song in the playlist");
        }
    }

    @Override
    public void newPlaylist(String name) {
        this.playlists.add(this.manager.createNewPlaylist(name));

    }

    @Override
    public void addSongToPlaylist(Song song, Playlist playlist) {
        // TODO Auto-generated method stub

    }

    @Override
    public void selectSong(Song song) {
        // System.out.println("select "+ song.getAbsolutePath());
        this.player.setPlayer(song); 
        this.player.play();
    }

    public ObservableList<Playlist> getObservablePlaylists() {
        return this.playlists;
    }

    @Override
    public void moveToMoment(Double percentage) {
        this.player.cue((int) ((percentage*player.getLength())/100));
    }
}