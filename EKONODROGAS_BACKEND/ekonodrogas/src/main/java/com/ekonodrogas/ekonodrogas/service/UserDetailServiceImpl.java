package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.persistence.RoleEntity;
import com.ekonodrogas.ekonodrogas.persistence.UserEntity;
import com.ekonodrogas.ekonodrogas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    @Autowired
    public UserDetailServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        for (RoleEntity role : user.getRoles()){
            authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getNombre())));
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                authorityList);
    }
}
