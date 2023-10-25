package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean artistprest=false;
        Artist artist1=null;
        for(Artist artist:artists){
            if(artist.getName().equals(artistName)){
                artistprest=true;
                artist1=artist;
                break;
            }
        }
        if(!artistprest){
            artist1=createArtist(artistName);
        }
        Album album=new Album(title);
        albums.add(album);
        if(artistAlbumMap.containsKey(artist1)){
            artistAlbumMap.get(artist1).add(album);
        }
        else{
            artistAlbumMap.put(artist1,new ArrayList<>());
            artistAlbumMap.get(artist1).add(album);
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean albumNamePresent=false;
        Album album2=null;
        for(Album album:albums){
            if(album.getTitle().equals(albumName)){
                albumNamePresent=true;
                album2=album;
                break;
            }
        }
        if(!albumNamePresent)throw new Exception("Album does not exist");
        Song song=new Song(title,length);
        songs.add(song);
        if(albumSongMap.containsKey(album2)){
            albumSongMap.get(album2).add(song);
        }
        else{
            albumSongMap.put(album2,new ArrayList<>());
            albumSongMap.get(album2).add(song);
        }
        if(!songLikeMap.containsKey(song)){
            songLikeMap.put(song,new ArrayList<>());
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist=new Playlist(title);
        boolean userpresent=false;
        User user1=null;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                userpresent=true;
                user1=user;
                break;
            }
        }
        if(!userpresent){
            throw new Exception("User does not exist");
        }
        if(playlistSongMap.containsKey(playlist)){
            for(Song song:songs){
                if(song.getLength()==length){
                    playlistSongMap.get(playlist).add(song);
                }
            }
        }
        else{
            playlistSongMap.put(playlist,new ArrayList<>());
            for(Song song:songs){
                if(song.getLength()==length){
                    playlistSongMap.get(playlist).add(song);
                }
            }
        }
        if(playlistListenerMap.containsKey(playlist)){
            playlistListenerMap.get(playlist).add(user1);
        }
        else{
            playlistListenerMap.put(playlist,new ArrayList<>());
            playlistListenerMap.get(playlist).add(user1);
        }
        creatorPlaylistMap.put(user1,playlist);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        Playlist playlist=new Playlist(title);
        boolean userpresent=false;
        User user1=null;
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                userpresent=true;
                user1=user;
                break;
            }
        }
        if(!userpresent){
            throw new Exception("User does not exist");
        }
        List<Song> dbsongs=new ArrayList<>();
        for(Song song:songs){
            String s=song.getTitle();
            for(String s1:songTitles){
                if(s.equals(s1))dbsongs.add(song);
            }
        }
        playlistSongMap.put(playlist,dbsongs);
        if(playlistListenerMap.containsKey(playlist)){
            playlistListenerMap.get(playlist).add(user1);
        }
        else{
            playlistListenerMap.put(playlist,new ArrayList<>());
            playlistListenerMap.get(playlist).add(user1);
        }
        creatorPlaylistMap.put(user1,playlist);
        playlists.add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist=null;
        for(Playlist p:playlists){
            if(p.getTitle().equals(playlistTitle)){
                playlist=p;
                break;
            }
        }
        if(playlist==null)throw new Exception("Playlist does not exist");
        User user=null;
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user=u;
                break;
            }
        }
        if(user==null)throw new Exception("User does not exist");
        List<User> list=playlistListenerMap.get(playlist);
        boolean userpresent=false;
        for(User u:list){
            if(u.equals(user)){
                userpresent=true;
                break;
            }
        }
        if(!userpresent){
            playlistListenerMap.get(playlist).add(user);
        }
        if(!creatorPlaylistMap.containsKey(user)){
            creatorPlaylistMap.put(user,playlist);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=null;
        Song song=null;
        for(User u:users){
            if(u.getMobile().equals(mobile)){
                user=u;
                break;
            }
        }
        for(Song s:songs){
            if(s.getTitle().equals(songTitle)){
                song=s;
                break;
            }
        }
        if(user==null)throw new Exception("User does not exist");
        if(song==null)throw new Exception("Song does not exist");
        boolean userliked=false;
        List<User> list=songLikeMap.get(song);
        for(User u:list){
            if(u.equals(user)){
                userliked=true;
                break;
            }
        }
        if(!userliked){
            songLikeMap.get(song).add(user);
            song.setLikes(song.getLikes()+1);
        }
        Album albummain=null;
        for(Album album:albumSongMap.keySet()){
            List<Song> songlist=albumSongMap.get(album);
            for(Song s:songlist){
                if(s.equals(song)){
                    albummain=album;
                    break;
                }
            }
            if(albummain!=null)break;
        }
        Artist artist=null;
        for(Artist a:artistAlbumMap.keySet()){
            List<Album> albumList=artistAlbumMap.get(a);
            for(Album al:albumList){
                if(al.equals(albummain)){
                    artist=a;
                    break;
                }
            }
            if(artist!=null)break;
        }
        artist.setLikes(artist.getLikes()+1);
        return song;
    }

    public String mostPopularArtist() {
        Artist artist=null;
        int maxlikes=0;
        for(Artist a:artists){
            int presentartistlikes=a.getLikes();
            if(presentartistlikes>maxlikes){
                maxlikes=presentartistlikes;
                artist=a;
            }
        }
        if(artist==null)return "Artist not found Exception";
        return artist.getName();
    }

    public String mostPopularSong() {
        Song song=null;
        int maxlikes=0;
        for(Song s:songs){
            int presentsonglikes=s.getLikes();
            if(presentsonglikes>maxlikes){
                maxlikes=presentsonglikes;
                song=s;
            }
        }
        if(song==null)return "Song not found Exception";
        return song.getTitle();
    }
}
