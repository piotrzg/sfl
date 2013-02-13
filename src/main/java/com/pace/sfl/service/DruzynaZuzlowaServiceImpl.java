package com.pace.sfl.service;


import com.pace.sfl.domain.DruzynaZuzlowa;
import com.pace.sfl.repository.DruzynaZuzlowaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class DruzynaZuzlowaServiceImpl implements DruzynaZuzlowaService {
    @Autowired
    DruzynaZuzlowaRepository druzynaZuzlowaRepository;

    public DruzynaZuzlowa updateDruzynaZuzlowa(DruzynaZuzlowa druzynaZuzlowa) {
        return druzynaZuzlowaRepository.save(druzynaZuzlowa);
    }

    public void saveDruzynaZuzlowa(DruzynaZuzlowa druzynaZuzlowa) {
        druzynaZuzlowaRepository.save(druzynaZuzlowa);
    }

    public List<DruzynaZuzlowa> findDruzynaZuzlowaEntries(int firstResult, int maxResults) {
        return druzynaZuzlowaRepository.findAll(new org.springframework.data.domain.PageRequest(firstResult / maxResults, maxResults)).getContent();
    }

    public List<DruzynaZuzlowa> findAllDruzynaZuzlowas() {
        return druzynaZuzlowaRepository.findAll();
    }

    public DruzynaZuzlowa findDruzynaZuzlowa(int tid) {
        List<DruzynaZuzlowa> dzs = druzynaZuzlowaRepository.findAll();
        Iterator<DruzynaZuzlowa> dzsIter = dzs.iterator();
        while(dzsIter.hasNext())
        {
            DruzynaZuzlowa dz = dzsIter.next();
            if(dz.getTid() == tid)
                return dz;
        }

        return null;
    }

    public DruzynaZuzlowa findDruzynaZuzlowa(BigInteger id) {
        return druzynaZuzlowaRepository.findOne(id);
    }

    public void deleteDruzynaZuzlowa(DruzynaZuzlowa druzynaZuzlowa) {
        druzynaZuzlowaRepository.delete(druzynaZuzlowa);
    }

    public long countAllDruzynaZuzlowas() {
        return druzynaZuzlowaRepository.count();
    }
}
