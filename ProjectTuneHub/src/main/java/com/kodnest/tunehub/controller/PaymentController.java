package com.kodnest.tunehub.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.kodnest.tunehub.entity.Song;
import com.kodnest.tunehub.entity.User;
import com.kodnest.tunehub.serviceimpl.SongServiceImpl;
import com.kodnest.tunehub.serviceimpl.UserServiceImpl;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import jakarta.servlet.http.HttpSession;

@Controller
public class PaymentController {
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	SongServiceImpl songServiceImpl;

	@GetMapping("/pay")
	public String pay() {
		return "pay";
	}

	@SuppressWarnings("finally")
	@PostMapping("/createOrder")
	@ResponseBody
	public String createOrder(HttpSession session) {

		int  amount  = 299;
		Order order=null;
		try {
			RazorpayClient razorpay=new RazorpayClient("rzp_test_XEpO7zQzzsnXb3" , "0ml0PuIh4HH1B3DPmX4vdlJq");

			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", amount*100); // amount in the smallest currency unit
			orderRequest.put("currency", "INR");
			orderRequest.put("receipt", "order_rcptid_11");
			order = razorpay.orders.create(orderRequest);

		} catch (RazorpayException e) {
			e.printStackTrace();
		}
		finally {
			return order.toString();
		}
	}
	
	
	@PostMapping("/verify")
	@ResponseBody
	public boolean verifyPayment(@RequestParam  String orderId, @RequestParam String paymentId,
											@RequestParam String signature) {
	    try {
	        // Initialize Razorpay client with your API key and secret
	        RazorpayClient razorpayClient = new RazorpayClient("rzp_test_XEpO7zQzzsnXb3", "0ml0PuIh4HH1B3DPmX4vdlJq");
	        // Create a signature verification data string
	        String verificationData = orderId + "|" + paymentId;

	        // Use Razorpay's utility function to verify the signature
	        boolean isValidSignature = Utils.verifySignature(verificationData, signature, "0ml0PuIh4HH1B3DPmX4vdlJq");

	        return isValidSignature;  //key value=signature
	    } 
	    
	    catch (RazorpayException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	//payment-success -> update to premium user
	@GetMapping("/payment-success")
	public String paymentSuccess(HttpSession session,Model model){
		
		String email = (String) session.getAttribute("email");
		User user = userServiceImpl.getUser(email);
		user.setIspremium(true);
		userServiceImpl.updateUser(user);
		List <Song> allSongs = songServiceImpl.fetchAllSongs();
		model.addAttribute("songs", allSongs);
		return "displaysongs";
	}
	
	
	//payment-failure -> redirect to login 
	@GetMapping("/payment-failure")
	public String paymentFailure(){
		//payment-error page
		return "customerhome";
	}		
}