// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.pace.sfl.repository;

import com.pace.sfl.domain.SflDruzyna;
import com.pace.sfl.repository.SflDruzynaRepository;
import java.math.BigInteger;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

privileged aspect SflDruzynaRepository_Roo_Mongo_Repository {
    
    declare parents: SflDruzynaRepository extends PagingAndSortingRepository<SflDruzyna, BigInteger>;
    
    declare @type: SflDruzynaRepository: @Repository;
    
}
