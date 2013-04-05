package com.pace.sfl.service;

/**
 * Created with IntelliJ IDEA.
 * User: Piotr
 * Date: 1/26/13
 * Time: 9:13 PM
 * To change this template use File | Settings | File Templates.
 */

import com.pace.sfl.Role;
import com.pace.sfl.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomMongoSecurityService implements UserDetailsService {

    private Account user;

    @Autowired
    AccountService userService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        try {

            System.out.println("loadUserByUsername: "+username);
            this.user = userService.findByUsername(username);
            boolean enabled = true;
            boolean accountNonExpired = true;
            boolean credentialsNonExpired = true;
            boolean accountNonLocked = true;

            return new User
                    (

                            this.user.getUsername(),
                            this.user.getPassword(),
                            enabled,
                            accountNonExpired,
                            credentialsNonExpired,
                            accountNonLocked,
                            getAuthorities()
                    );

        } catch (Exception e) {
            System.out.println("e: "+e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public Collection getAuthorities() {
        List authList = getGrantedAuthorities();
        return authList;
    }

    public List getGrantedAuthorities() {

        List authorities = new ArrayList();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getRole()));
        }

        return authorities;
    }
}
