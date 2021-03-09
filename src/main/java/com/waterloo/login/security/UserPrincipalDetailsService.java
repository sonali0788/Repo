package com.waterloo.login.security;

import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waterloo.login.dao.UserRepository;
import com.waterloo.login.model.User;

@Service
@Transactional
public class UserPrincipalDetailsService implements UserDetailsService {

	public static final int MAX_FAILED_ATTEMPTS = 3;

	private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours

	private UserRepository userRepository;

	public UserPrincipalDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
		User user = this.userRepository.findByUsername(s);
		UserPrincipal userPrincipal = new UserPrincipal(user);

		return userPrincipal;
	}

	// code login attempts starts here
	public void increaseFailedAttempts(User user) {
		int newFailAttempts = user.getFailedAttempt() + 1;
		userRepository.updateFailedAttempts(newFailAttempts, user.getUsername());
	}

	public void resetFailedAttempts(String username) {
		userRepository.updateFailedAttempts(0, username);
	}

	public void lock(User user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	public boolean unlockWhenTimeExpired(User user) {
		long lockTimeInMillis = user.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis();
		if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempt(0);

			userRepository.save(user);

			return true;
		}

		return false;
	}

}
