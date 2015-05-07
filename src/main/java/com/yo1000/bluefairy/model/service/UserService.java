package com.yo1000.bluefairy.model.service;

import com.yo1000.bluefairy.model.entity.User;
import com.yo1000.bluefairy.model.entity.UserDetailsWithSalt;
import com.yo1000.bluefairy.model.repository.UserRepository;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 15/03/24.
 */
@Service
public class UserService implements UserDetailsService {
    @Resource
    private UserRepository userRepository;

    @Resource
    private ShaPasswordEncoder shaPasswordEncoder;

    @Resource
    private SaltSource saltSource;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.getUserRepository().findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(
                    String.format("\"%1$s\" is not found.", username));
        }

        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority(user.getRole()));

        return new UserDetailsWithSalt(user.getUsername(), user.getPassword(),
                user.getSalt(), authorities);
    }

    public boolean existsUser() {
        return this.getUserRepository().count() > 0L;
    }

    public void registerUser(String username, String password) {
        byte[] rnd = new byte[32];
        new SecureRandom().nextBytes(rnd);
        String salt = String.valueOf(Hex.encode(rnd));
        String role = "ADMIN";

        User user = new User();
        user.setUsername(username);
        user.setPassword(this.encodePassword(username, password, salt, role));
        user.setSalt(salt);
        user.setRole(role);

        this.getUserRepository().create(user);
    }

    protected String encodePassword(String username, String password, String salt, String role) {
        UserDetails userDetails = new UserDetailsWithSalt(username, password, salt,
                Arrays.asList(new SimpleGrantedAuthority(role)));

        return this.getShaPasswordEncoder().encodePassword(password,
                this.getSaltSource().getSalt(userDetails));
    }

    protected UserRepository getUserRepository() {
        return userRepository;
    }

    protected ShaPasswordEncoder getShaPasswordEncoder() {
        return shaPasswordEncoder;
    }

    protected SaltSource getSaltSource() {
        return saltSource;
    }
}
