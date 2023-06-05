package com.dwt.photovoltaic.Photovoltaic.System.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.Collection;

@Document("User")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private ObjectId id;
    private String fullName;
    private String emailId;
    private String username;
    private String password;
    private String contactNo;
    private String userType;

//    public User(String fullName, String emailId, String username, String password, String contactNo, String userType) {
//        super();
//        this.fullName = fullName;
//        this.emailId = emailId;
//        this.username = username;
//        this.password = password;
//        this.contactNo = contactNo;
//        this.userType = userType;
//    }
//
//    public User() {
//
//    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//    public String getFullName() {
//        return fullName;
//    }
//
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
//
//    public String getEmailId() {
//        return emailId;
//    }
//
//    public void setEmailId(String emailId) {
//        this.emailId = emailId;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getContactNo() {
//        return contactNo;
//    }
//
//    public void setContactNo(String contactNo) {
//        this.contactNo = contactNo;
//    }
//
//    public String getUserType() {
//        return userType;
//    }
//
//    public void setUserType(String userType) {
//        this.userType = userType;
//    }
//    @Override
//    public String toString() {
//        return "User{" +
//                ", fullName='" + fullName + '\'' +
//                ", emailId='" + emailId + '\'' +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", contactNo='" + contactNo + '\'' +
//                ", userType='" + userType + '\'' +
//                '}';
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
