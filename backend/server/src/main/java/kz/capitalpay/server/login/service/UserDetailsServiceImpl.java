package kz.capitalpay.server.login.service;


import com.google.gson.Gson;
import kz.capitalpay.server.login.model.ApplicationUser;
import kz.capitalpay.server.login.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    Gson gson;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Username: {}", username);
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);
        logger.info(gson.toJson(applicationUser));
        if (applicationUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getPassword(),
                applicationUser.getRoles());
    }


}
