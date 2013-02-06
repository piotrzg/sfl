package com.pace.sfl.service;

import com.pace.sfl.Utils.Utils;
import com.pace.sfl.domain.Account;
import com.pace.sfl.domain.UserProfile;
import com.pace.sfl.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    public Account findByUsername(String username)
    {
        Account acc = mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), Account.class);
        return acc;
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    public String saveAccount(Account account) {
        List authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        System.out.println("Account: "+account);
        if(account.getPassword().equals(account.getRetypePassword()))
        {
            if(Utils.isEmail(account.getEmail()))
            {
                account.setRoles(authorities);
                accountRepository.save(account);

                UserProfile up = new UserProfile();
                up.setUserAccount(account);
                ups.saveUserProfile(up);
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
}
