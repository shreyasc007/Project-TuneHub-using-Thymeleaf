package com.kodnest.tunehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kodnest.tunehub.entity.Playlist;
import com.kodnest.tunehub.entity.Song;
import com.kodnest.tunehub.serviceimpl.PlaylistServiceImpl;
import com.kodnest.tunehub.serviceimpl.SongServiceImpl;

@Controller
public class PlaylistController {
	
	@Autowired
	SongServiceImpl songServiceImpl;
	
	@Autowired
	PlaylistServiceImpl playlistServiceImpl;

	//model stores the list of songs coming from the db and render it back to the FE part
	@GetMapping("/createplaylists")
	public String createPlaylist(Model model) {
		List <Song> songList = songServiceImpl.fetchAllSongs();
		model.addAttribute("songs", songList);
		return "createplaylists";
	}
	
	@PostMapping("/addplaylist")
	public String addPlaylist(@ModelAttribute Playlist playlist) {
		
		//updating the playlist table
		playlistServiceImpl.addPlaylist(playlist);
		
		//updating the song table
		//based on M2M
		List<Song> songList = playlist.getSongs();
		for(Song s:songList) {
			s.getPlaylists().add(playlist);//add=save()
			songServiceImpl.updateSong(s);
		}
		return "adminhome";
	}
	
	@GetMapping("/viewplaylists")
	public String viewPlaylists(Model model) {
		List<Playlist> playlists = playlistServiceImpl.fetchAllPlaylist();
		model.addAttribute("playlist", playlists);
		return "displayplaylists";
		
	}
}
