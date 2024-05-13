package com.webprojectSEA.WebBlogProject.Services.UserServices;

import com.webprojectSEA.WebBlogProject.model.UserAccount;

public interface UserProfileService {


   UserAccount getUserProfileById(Long id);
   UserAccount createUserProfile(UserAccount userAccount);
   UserAccount updateUserProfile(Long id, UserAccount userAccount);
   boolean deleteUserProfile(Long id);

}




