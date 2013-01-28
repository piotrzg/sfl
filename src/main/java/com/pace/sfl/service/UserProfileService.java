package com.pace.sfl.service;

import com.pace.sfl.domain.UserProfile;
import org.springframework.roo.addon.layers.service.RooService;

@RooService(domainTypes = { com.pace.sfl.domain.UserProfile.class })
public interface UserProfileService {
    public UserProfile findByUsername(String username);
}
