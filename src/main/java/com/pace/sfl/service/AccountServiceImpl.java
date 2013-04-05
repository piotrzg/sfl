package com.pace.sfl.service;

import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.Account;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserProfileService ups;

    @Autowired
    private MailSender mailSender;
    @Autowired
    private SimpleMailMessage templateMessage;

    public Account findByUsername(String username)
    {
        Account acc = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), Account.class);

        if(acc == null)
        {
            acc = mongoTemplate.findOne(Query.query(Criteria.where("email").is(username)), Account.class);
            if(acc == null)
                return null;
        }

        return acc;
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public String saveAccount(Account account) {

        String errMsg = checkIfDuplicateAccount(account);
        if(errMsg != null)
            return errMsg;

        List authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if(account.getPassword().equals(account.getRetypePassword()))
        {
            if(Utils.isEmail(account.getEmail()))
            {
                account.setRoles(authorities);
                PasswordEncoder encoder = new Md5PasswordEncoder();
                String hashedPass = encoder.encodePassword(account.getPassword(), null);
                account.setPassword(hashedPass);
                String emailStr = account.getEmail().toLowerCase();
                account.setEmail(emailStr);
                accountRepository.save(account);

                UserProfile up = new UserProfile();
                up.setUserAccount(account);
                ups.saveUserProfile(up);

                // Create a thread safe "copy" of the template message and customize it
                SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
                msg.setTo(account.getEmail());
                msg.setText("Witamy w serwisie Speedway Fantasy!\nTwój login: "+account.getUsername());
                try{
                    this.mailSender.send(msg);
                }
                catch(MailException ex) {
                    // simply log it and go on...
                    System.err.println(ex.getMessage());
                }

                return "good";
            }
            else
            {
                return "error_invalid_email";
            }
        }
        else
        {
            return "error_hasla";
        }
    }

    public List<Account> findAccountEntries(int firstResult, int maxResults) {
        return accountRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    public Account findAccount(BigInteger id) {
        return accountRepository.findOne(id);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    public long countAllAccounts() {
        return accountRepository.count();
    }


    private String checkIfDuplicateAccount(Account account)
    {
        String desiredUsername = account.getUsername();
        Account acc = mongoTemplate.findOne(Query.query(Criteria.where("username").regex('^'+desiredUsername+'$', "i")), Account.class);
        if(acc == null)
        {
            acc = mongoTemplate.findOne(Query.query(Criteria.where("email").is(account.getEmail())), Account.class);

            if(acc != null)
                return "error_duplicate_email";
        }
        else{
            return "error_duplicate_username";
        }

        return null;
    }
}
