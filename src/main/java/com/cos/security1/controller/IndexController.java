package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")// authentication을 DI(의존성 주입) 하면 안에 Principal이 존재한다.
	public @ResponseBody String testlogin(Authentication authentication,
			@AuthenticationPrincipal UserDetails userDetails) { 
		System.out.println("/test/login ===========");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("principalDetails : " + principalDetails);
		System.out.println("principalDetails.user : "+ principalDetails.getUser());
		System.out.println("userDetails : "+userDetails);
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthlogin(Authentication authentication,
			@AuthenticationPrincipal OAuth2User oauth) { 
		System.out.println("/test/oauth/login ===========");
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("oAuth2User.getAttributes : " + oAuth2User.getAttributes());
		System.out.println("oAuth2User : "+oauth.getAuthorities());
		
		return "세션 정보 확인하기";
	}
	
	@GetMapping({ "", "/" })
	public @ResponseBody String index() {
		return "인덱스 페이지입니다.";
	}
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	@GetMapping("/joinForm") 
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user);
		
		return "redirect:/loginForm";
	}
	
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails.getUser = "+principalDetails.getUser());
		return "user";
	}
	
	@Secured("ROLE_ADMIN") // 권한이 1개일때 사용
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 여러개의 권한을 사용하고 싶을때 사용
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터정보";
	}

}
