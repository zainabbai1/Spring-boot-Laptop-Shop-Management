package com.laptop.springboot.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.laptop.springboot.model.Role;
import com.laptop.springboot.model.User;
import com.laptop.springboot.model.UserRegistrationDto;
import com.laptop.springboot.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService
{

	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public UserServiceImpl(UserRepository userRepository) 
	{
		this.userRepository = userRepository;
	}

	@Override
	public User save(UserRegistrationDto registrationDto) 
	{
		User user = new User(registrationDto.getName(), 
				registrationDto.getNumber(), registrationDto.getEmail(),registrationDto.getAddress(),
				passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(new Role("ROLE_USER")));
		
		return userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
	{	
		User user = userRepository.findByEmail(username);
		
		System.out.println("user details - > "+user);
		if(user == null) 
		{
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));		
	}
	
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles)
	{
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
	
	@Override
	public User getUserDetails(String username) {
		User user = userRepository.findByEmail(username);
		return user;
	}
}
