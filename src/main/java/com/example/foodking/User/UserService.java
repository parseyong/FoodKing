package com.example.foodking.User;

import com.example.foodking.Auth.JwtProvider;
import com.example.foodking.Exception.CommondException;
import com.example.foodking.Exception.ExceptionCode;
import com.example.foodking.User.DTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    public String login(LoginReqDTO loginReqDTO){

        User user = userRepository.findUserByEmail(loginReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.LOGIN_FAIL));

        isMatchPassword(loginReqDTO.getPassword(), user.getPassword(), ExceptionCode.LOGIN_FAIL);
        return jwtProvider.createToken(user.getUserId(), user.getAuthorities());
    }
    @Transactional
    public void addUser(AddUserReqDTO addUserReqDTO){

        if(emailDuplicatedChecking(addUserReqDTO.getEmail()))
           throw new CommondException(ExceptionCode.EMAIL_DUPLICATED);
        if(nickNameDuplicatedChecking(addUserReqDTO.getNickName()))
            throw new CommondException(ExceptionCode.NICKNAME_DUPLICATED);
        if(!addUserReqDTO.getPassword().equals(addUserReqDTO.getPasswordRepeat()))
            throw new CommondException(ExceptionCode.PASSWORD_NOT_COLLECT);

        addUserReqDTO.setPassword(passwordEncoder.encode(addUserReqDTO.getPassword()));
        User user = AddUserReqDTO.toEntity(addUserReqDTO);
        userRepository.save(user);
    }

    public boolean emailDuplicatedChecking(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean nickNameDuplicatedChecking(String nickName){
        return userRepository.existsByNickName(nickName);
    }

    public String findEmail(String phoneNum){
        return userRepository.findEmailByPhoneNum(phoneNum)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }

    public String findPassword(String email){
        return userRepository.findPasswordByEmail(email)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));
    }

    public ReadUserInfoResDTO readUserInfo(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        return ReadUserInfoResDTO.toDTO(user);
    }
    @Transactional
    public User updateUserInfo(UpdateUserInfoReqDTO updateUserInfoReqDTO,Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        isMatchPassword(updateUserInfoReqDTO.getOldPassword(),user.getPassword()
                ,ExceptionCode.PASSWORD_NOT_COLLECT);

        user.changeNickName(updateUserInfoReqDTO.getNickName());
        user.changePhoneNum(updateUserInfoReqDTO.getPhoneNum());
        user.changePassword(passwordEncoder.encode(updateUserInfoReqDTO.getNewPassword()));
        userRepository.save(user);
        return user;
    }
    @Transactional
    public void deleteUser(DeleteUserReqDTO deleteUserReqDTO){
        User user = userRepository.findUserByEmail(deleteUserReqDTO.getEmail())
                .orElseThrow(() -> new CommondException(ExceptionCode.NOT_EXIST_USER));

        isMatchPassword(deleteUserReqDTO.getPassword(), user.getPassword(), ExceptionCode.PASSWORD_NOT_COLLECT);
        userRepository.delete(user);
    }

    //password1에는 인코딩 되지않은 값을, password2에는 인코딩이 되어있는 값을 넣어야한다.
    public void isMatchPassword(String password1, String password2, ExceptionCode exceptionCode){

        if(!passwordEncoder.matches(password1,password2))
            throw new CommondException(exceptionCode);
    }
}
