// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.pace.sfl.repository;

import com.pace.sfl.domain.ZawodnikZuzlowy;
import com.pace.sfl.repository.ZawodnikZuzlowyRepository;
import java.math.BigInteger;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

privileged aspect ZawodnikZuzlowyRepository_Roo_Mongo_Repository {
    
    declare parents: ZawodnikZuzlowyRepository extends PagingAndSortingRepository<ZawodnikZuzlowy, BigInteger>;
    
    declare @type: ZawodnikZuzlowyRepository: @Repository;
    
}
