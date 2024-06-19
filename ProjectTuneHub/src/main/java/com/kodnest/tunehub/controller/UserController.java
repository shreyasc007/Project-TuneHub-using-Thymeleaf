package com.kodnest.tunehub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kodnest.tunehub.entity.Song;
import com.kodnest.tunehub.entity.User;
import com.kodnest.tunehub.serviceimpl.SongServiceImpl;
import com.kodnest.tunehub.serviceimpl.UserServiceImpl;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	SongServiceImpl songServiceImpl;
	
	@PostMapping("/register")
	public String addUser(@ModelAttribute User user) {
//		System.out.println(user.getUsername()+" "+user.getEmail()+" "+user.getPassword()+" "+user.getGender()+" "
//	                           +user.getRole()+" "+user.getAddress());
		
		//email taken from the registration form
		String email = user.getEmail();
		
		//checking the entered mail in DB
		boolean status=userServiceImpl.emailExists(email);
		
		if (status == false) {
			userServiceImpl.addUser(user);
			System.out.println("User added");
		}
		
		else {
			System.out.println("User already exists");
		}
		
		return "login";
	}
	
	@PostMapping("/validate")
	public String validate(@RequestParam("email") String email,
			@RequestParam("password") String password, HttpSession session, Model model){
		
		if(userServiceImpl.validateUser(email,password)==true) {
			
			String role = userServiceImpl.getRole(email);
			
			session.setAttribute("email", email);
			
			if(role.equals("admin")){
				return "adminhome";
			}
			
			else {
				User user = userServiceImpl.getUser(email);
				boolean userStatus = user.getIspremium();
				model.addAttribute("isPremium", userStatus);
				
				List<Song> allSongs = songServiceImpl.fetchAllSongs();
				model.addAttribute("songs", allSongs);
				
				return "customerhome";
			}
		}
		
		else {
			return "login";
		}
		
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();//terminates the session
		return "login";
	}
	
	

}
