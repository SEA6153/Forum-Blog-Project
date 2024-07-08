package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.Repostories.UserAccountRepository;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final UserAccountRepository userAccountRepository;

    public UserProfileServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }


    @Override
    public UserAccount getUserProfileById(Long id) {
        return userAccountRepository.findById(id).orElse(null);
    }

    @Override
    public UserAccount createUserProfile(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    @Override
    public UserAccount updateUserProfile(Long id, UserAccount userAccount) {
        UserAccount existingUserProfile = userAccountRepository.findById(id).orElse(null);
        if (existingUserProfile != null) {

            existingUserProfile.setFirstName(userAccount.getFirstName());
            existingUserProfile.setLastName(userAccount.getLastName());
            existingUserProfile.setNickname(userAccount.getNickname());
            existingUserProfile.setEmail(userAccount.getEmail());
            // vb...
            return userAccountRepository.save(existingUserProfile);
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteUserProfile(Long id) {
        if (userAccountRepository.existsById(id)) {
            userAccountRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

