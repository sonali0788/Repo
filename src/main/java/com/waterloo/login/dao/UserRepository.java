package com.waterloo.login.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.waterloo.login.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
	
	  @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.username = ?2")
	  
	  @Modifying public void updateFailedAttempts(int failAttempts, String
	  username);
	 
}
