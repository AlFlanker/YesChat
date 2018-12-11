package SimpleYesChat.YesChat.Services;

import SimpleYesChat.YesChat.UserData.UserData;
import SimpleYesChat.YesChat.domain.Role;
import SimpleYesChat.YesChat.domain.User;
import SimpleYesChat.YesChat.domain.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRegistry sessionRegistry;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }
    public User lastVisit(@AuthenticationPrincipal User user){
        user.setLastVisit(LocalDateTime.now());
        userRepo.save(user);
        return user;
    }

    public User addNewUser(UserData userData){
        if(userRepo.findByUsername(userData.getLogin())==null){
            User user = new User();
            user.setActive(true);
            user.setLastVisit(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(userData.getPass()));
            user.setCookies(userData.getCookies());
            user.setUsername(userData.getLogin());
            user.setRoles(Collections.singleton(Role.USER));
            user.setUserID(userData.getId());
            userRepo.save(user);
            return user;
        }
        else{
            User user = userRepo.findByUsername(userData.getLogin());
            user.setCookies(userData.getCookies());
            if(!passwordEncoder.matches(userData.getPass(),user.getPassword())) {
                user.setPassword(passwordEncoder.encode(userData.getPass()));
            }
            user.setLastVisit(LocalDateTime.now());
            user.setActive(true);
            userRepo.save(user);
            return user;
        }
    }

    public boolean login(UserData user){
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(user.getLogin(),user.getPass());
        Authentication auth = authManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
//
//        HttpSession session = request.getSession();
//        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
        return true;
    }
}
