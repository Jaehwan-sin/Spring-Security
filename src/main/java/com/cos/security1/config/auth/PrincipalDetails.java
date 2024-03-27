package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어준다. ( Security ContextHolder )
// 오브젝트 => Authentication 타입의 객체여야 한다.
// Authentication 안에 User 정보가 있어야 됨.
// User 오브젝트 타입 => UserDetails 타입 객체

// Security Session에 저장하는데 Authentication 타입이어야하고 이 안에 User는 UserDetails 타입이어야한다.
// UserDetails를 상속 받으면 PrincipalDetails = UserDetails이다.

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {
	
	private User user;
	private Map<String, Object> attributes; // OAuth2User에 담긴 정보를 Map에 다 담기위해 변수 설정
	
	// 일반 로그인 시 사용하는 생성자
	public PrincipalDetails(User user) {
		this.user = user;
	}
	
	// OAuth 로그인 시 사용하는 생성자
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attributes = attributes;
	}
	
	// 해당 User의 권한을 리턴하는 곳!
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		// 우리 사이트에서 1년동안 로그인을 하지 않았다면 return false; 로 설정한다.
		// 위처럼 설정할 경우 UserEntity에 LoginDate를 설정해서 현재날짜 - LoginDate가 1년이 넘었다면 return false;
		return true;
	}
	
	// OAuth2User 하면 오버라이딩된다.
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return null;
	}

}
