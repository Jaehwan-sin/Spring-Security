package com.cos.security1.config.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수
// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("userRequest : " +userRequest);
		System.out.println("getClientRegistration : " +userRequest.getClientRegistration()); // registraionId로 어떤 OAuth로 로그인 했는지 확인 가능
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		// 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> code 리턴(OAuth2-Client 라이브러리) -> AccessToken 요청 
		System.out.println("getAccessToken : " +userRequest.getAccessToken());
		System.out.println("oAuth2User.getAttributes() : " +oAuth2User.getAttributes());
		
		// 회원가입 강제로 진행
		String provider = userRequest.getClientRegistration().getClientId(); // google
		String providerID = oAuth2User.getAttribute("sub");
		String username = provider+"_"+providerID; 
		String password = bCryptPasswordEncoder.encode("겟인데어");
		String email = oAuth2User.getAttribute("email");
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if (userEntity == null) {
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerID(providerID)
					.build();
			userRepository.save(userEntity);
		} 
		
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
	}
}
