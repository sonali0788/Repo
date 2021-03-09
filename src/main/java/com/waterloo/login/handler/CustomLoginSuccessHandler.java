package com.waterloo.login.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.waterloo.login.dao.UserRepository;
import com.waterloo.login.model.User;
import com.waterloo.login.security.UserPrincipalDetailsService;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserPrincipalDetailsService userService;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String username = request.getParameter("txtUsername");
//		userService = (UserPrincipalDetailsService) authentication.getPrincipal();
		User user = userRepository.findByUsername(username);
		if (user.getFailedAttempt() > 0) {
			userService.resetFailedAttempts(username);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

}