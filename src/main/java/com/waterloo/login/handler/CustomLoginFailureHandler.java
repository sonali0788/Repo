package com.waterloo.login.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.waterloo.login.dao.UserRepository;
import com.waterloo.login.model.User;
import com.waterloo.login.security.UserPrincipalDetailsService;

@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserPrincipalDetailsService userService;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = request.getParameter("txtUsername");
	
		User user = userRepository.findByUsername(username);
		
		

		if (user != null) {

			System.out.println(user.isAccountNonLocked());
			if (user.isAccountNonLocked()) {
				if (user.getFailedAttempt() < UserPrincipalDetailsService.MAX_FAILED_ATTEMPTS - 1) {
					userService.increaseFailedAttempts(user);
				} else {

					userService.lock(user);
					exception = new LockedException("Your account has been locked due to 3 failed attempts."
							+ " It will be unlocked after 24 hours.");
				}

			} else if (!user.isAccountNonLocked()) {

				if (userService.unlockWhenTimeExpired(user)) {

					exception = new LockedException("Your account has been unlocked. Please try to login again.");
				}
			}
		}

		super.setDefaultFailureUrl("/login?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}