// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.pace.sfl.repository;

import com.pace.sfl.domain.Account;
import com.pace.sfl.repository.AccountRepository;
import java.math.BigInteger;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

privileged aspect AccountRepository_Roo_Mongo_Repository {
    
    declare parents: AccountRepository extends PagingAndSortingRepository<Account, BigInteger>;
    
    declare @type: AccountRepository: @Repository;
    
}
