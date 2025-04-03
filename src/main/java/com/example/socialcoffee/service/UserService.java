package com.example.socialcoffee.service;

import com.tdt.cqsta.authserver.config.AuthConfig;
import com.tdt.cqsta.authserver.domain.*;
import com.tdt.cqsta.authserver.dto.UserLoginDTO;
import com.tdt.cqsta.authserver.dto.UserNewPasswordDTO;
import com.tdt.cqsta.authserver.dto.UserRegisterDTO;
import com.tdt.cqsta.authserver.dto.request.ConfirmPassword;
import com.tdt.cqsta.authserver.model.LoginSPhotonResponse;
import com.tdt.cqsta.authserver.model.UpdatePasswordSPhoton;
import com.tdt.cqsta.authserver.model.UserLoginChat;
import com.tdt.cqsta.authserver.repository.MongoCQFollowRelationRepository;
import com.tdt.cqsta.authserver.repository.MongoCQFriendRepository;
import com.tdt.cqsta.authserver.repository.MongoCQGroupPostRepository;
import com.tdt.cqsta.authserver.repository.UserRepository;
import com.tdt.cqsta.authserver.repository.mongo.MemberGroupInfoRepository;
import com.tdt.cqsta.common.constants.CommonConstant;
import com.tdt.cqsta.common.constants.EmailConstants;
import com.tdt.cqsta.common.constants.OTPConstant;
import com.tdt.cqsta.common.dto.MetaDTO;
import com.tdt.cqsta.common.dto.ResponseMetaData;
import com.tdt.cqsta.common.enums.*;
import com.tdt.cqsta.common.utils.ObjectUtil;
import com.tdt.cqsta.common.utils.PhoneUtils;
import com.tdt.cqsta.common.utils.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public final BCryptPasswordEncoder encoder;

    private final RedisTemplate<String, String> redisTemplate;

    private final AuthConfig authConfig;

    private final RedisAuthService redisAuthService;

    private final AESService aesService;

    private final SPhotonChatService sPhotonChatService;

    private final MongoCQGroupPostRepository mongoCQGroupPostRepository;

    private final MongoTemplate mongoTemplate;

    private final MongoCQFollowRelationRepository mongoCQFollowRelationRepository;

    private final MongoCQFriendRepository mongoCQFriendRepository;

    private final MemberGroupInfoRepository memberGroupInfoRepository;

    public UserEntity checkLogin(UserLoginDTO userLogin) {
        List<UserEntity> userList = new ArrayList<>();
        if (userLogin.getType().equalsIgnoreCase(LoginType.EMAIL.getValue()))
            userList = userRepository.findAllByEmailIgnoreCase(StringUtils.lowerCase(userLogin.getUsername()));

        if (userLogin.getType().equalsIgnoreCase(LoginType.PHONE.getValue())) {
            String phone = PhoneUtils.formatPhoneNumber(userLogin.getUsername(), authConfig.getListCountryCodeAllow());
            if (StringUtils.isNotBlank(phone))
                userList = userRepository.findAllByPhone(phone);
        }
        return getPriorityUser(userList);
    }

    private UserEntity getPriorityUser(List<UserEntity> userList) {
        if (CollectionUtils.isEmpty(userList)) return null;
        if (NumberUtils.INTEGER_ONE.equals(userList.size())) return userList.get(NumberUtils.INTEGER_ZERO);
        Map<Long, UserEntity> userMap = userList.stream().collect(Collectors.toMap(UserEntity::getSubSystem, Function.identity()));
        if (userMap.containsKey(SubSystem.POEM.getValue())) return userMap.get(SubSystem.POEM.getValue());
        if (userMap.containsKey(SubSystem.POEM_HK.getValue())) return userMap.get(SubSystem.POEM_HK.getValue());
        if (userMap.containsKey(SubSystem.CQ_STATION.getValue())) return userMap.get(SubSystem.CQ_STATION.getValue());
        return null;
    }

    public MetaDTO checkEmailAndPhoneBeforeRegister(UserRegisterDTO userRegister, Long subSystem) {
        if (StringUtils.isNotBlank(userRegister.getEmail())) {
            if (!isEmailOrPhoneVerified(userRegister.getEmail(), subSystem)) {
                log.warn("Email = {} is not verified, subSystem = {}", userRegister.getEmail(), subSystem);
                return new MetaDTO(MetaData.NOT_VERIFIED_REGISTER);
            }
            if (userRepository.existsByEmailIgnoreCase(userRegister.getEmail())) {
                log.warn("Email = {} is existing, subSystem = {}", userRegister.getEmail(), subSystem);
                return new MetaDTO(MetaData.EMAIL_EXIST);
            }
        } else {
            if (!isEmailOrPhoneVerified(userRegister.getPhone(), subSystem)) {
                log.warn("Phone = {} is not verified, subSystem = {}", userRegister.getPhone(), subSystem);
                return new MetaDTO(MetaData.NOT_VERIFIED_REGISTER);
            }
            if (userRepository.existsByPhone(userRegister.getPhone())) {
                log.warn("Phone = {} is existing, subSystem = {}", userRegister.getPhone(), subSystem);
                return new MetaDTO(MetaData.PHONE_EXIST);
            }
        }
        return null;
    }

    public MetaDTO checkEmailBeforeRegisterP3(String email, Long subSystem) {
        if (Strings.isBlank(email)) {
            log.warn("Email is missing, subSystem = {}", subSystem);
            return new MetaDTO(MetaData.EMAIL_MISSING);
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.warn("Email = {} is existing, subSystem = {}", email, subSystem);
            return new MetaDTO(MetaData.EMAIL_EXIST);
        }
        return null;
    }

    public UserEntity getUserByEmailOrPhone(UserNewPasswordDTO userNewPasswordDTO, Long subSystem) {
        UserEntity user;
        if (StringUtils.isNotBlank(userNewPasswordDTO.getEmail()))
            user = userRepository.findOneByEmailIgnoreCaseAndSubSystem(userNewPasswordDTO.getEmail(), subSystem);
        else user = userRepository.findOneByPhoneAndSubSystem(userNewPasswordDTO.getPhone(), subSystem);

        if (Objects.isNull(user) || !UserStatus.ACTIVE.getValue().equalsIgnoreCase(user.getStatus())) {
            log.warn("Email {} is not existing in subSystem = {}", userNewPasswordDTO.getEmail(), subSystem);
            return null;
        }
        return user;
    }

    public UserEntity getUserByEmail(String email, Long subSystem) {
        return userRepository.findOneByEmailIgnoreCaseAndSubSystem(email, subSystem);
    }

    public boolean updatePasswordAndDeleteOldToken(UserEntity userEntity, UserNewPasswordDTO userNewPasswordDTO, String phoneOrEmail, Long subSystem) {
        log.info("Start update password and delete old token with userId = {}, subSystem = {}", userEntity.getId(), subSystem);
        String oldPassword = userEntity.getPassword();
        userEntity.setPassword(encoder.encode(userNewPasswordDTO.getNewPassword()));
        if (!updatePasswordSPhotonUser(userEntity.getEmail(), oldPassword, userEntity.getPassword(), subSystem)) {
            log.warn("FAILED while update password with userId = {}, subSystem = {}", userEntity.getId(), subSystem);
            return false;
        }
        redisAuthService.updateRedisConfirmMailOrPhone(phoneOrEmail, EmailConstants.NOT_CONFIRM_EMAIL, subSystem.toString());
        userRepository.save(userEntity);
        deleteOldToken(String.valueOf(userEntity.getId()), subSystem);
        log.info("SUCCESS update password with userId = {}, subSystem = {}", userEntity.getId(), subSystem);
        return true;
    }

    public boolean updatePasswordSPhotonUser(String email, String oldPassword, String newPassword, Long subSystem) {
        log.info("Start update password sPhoton user with email = {}, subSystem = {}", email, subSystem);
        UpdatePasswordSPhoton updatePasswordSPhoton = new UpdatePasswordSPhoton(newPassword, oldPassword);
        LoginSPhotonResponse loginSPhoton = sPhotonChatService.loginSPhoton(new UserLoginChat(email, oldPassword, subSystem), subSystem);

        if (Objects.isNull(loginSPhoton)
                || Objects.isNull(loginSPhoton.getBody())
                || StringUtils.isAnyBlank(loginSPhoton.getToken(), loginSPhoton.getBody().getId())
                || !sPhotonChatService.updatePasswordUser(updatePasswordSPhoton, loginSPhoton.getToken(), loginSPhoton.getBody().getId(), subSystem)) {
            log.warn("FAILED while update password sPhoton user with email = {}, subSystem = {}", email, subSystem);
            return false;
        }
        sPhotonChatService.revokeAllSession(loginSPhoton.getToken(), loginSPhoton.getBody().getEmail(),
                subSystem, loginSPhoton.getBody().getId());
        log.info("SUCCESS while update password sPhoton user with email = {}, subSystem = {}", email, subSystem);
        return true;
    }

    public void deleteOldToken(String userId, Long subSystem) {
        log.info("Start delete old token with userId = {}", userId);
        Set<String> sPhotonChatTokenKeyList = redisTemplate.keys(
                RedisKeyUtil.getSPhotonChatTokenKeyListByUserId(authConfig.getPrefixRedisKey(), userId, subSystem));
        if (!CollectionUtils.isEmpty(sPhotonChatTokenKeyList))
            redisTemplate.delete(sPhotonChatTokenKeyList);

        Set<String> refreshTokenKeyList = redisTemplate.keys(
                RedisKeyUtil.getRefreshTokenKeyListByUserId(authConfig.getPrefixRedisKey(), userId, subSystem));
        if (!CollectionUtils.isEmpty(refreshTokenKeyList))
            redisTemplate.delete(refreshTokenKeyList);

        Set<String> accessTokenKeyList = redisTemplate.keys(
                RedisKeyUtil.getAccessTokenKeyListByUserId(authConfig.getPrefixRedisKey(), userId, subSystem));
        if (!CollectionUtils.isEmpty(accessTokenKeyList))
            redisTemplate.delete(accessTokenKeyList);
        log.info("SUCCESS delete old token with userId = {}", userId);
    }

    public boolean verifyOTP(String emailOrPhone, String otpEncrypt, String subSystem) {
        String redisOtpKey = RedisKeyUtil.getOtpKey(authConfig.getPrefixRedisKey(), subSystem, emailOrPhone);
        String redisConfirmKey = RedisKeyUtil.redisConfirmMailOrPhoneKey(authConfig.getPrefixRedisKey(), emailOrPhone, subSystem);

        if (aesService.decryptDataWithoutUrl(otpEncrypt).equals(redisTemplate.opsForValue().get(redisOtpKey))
                && OTPConstant.NOT_CONFIRM_OTP.equals(redisTemplate.opsForValue().get(redisConfirmKey))) {
            redisTemplate.opsForValue().set(
                    redisConfirmKey, OTPConstant.CONFIRMED_OTP,
                    authConfig.getExpireTimeConfirmOtpOrPassword(), TimeUnit.MILLISECONDS);
            redisTemplate.delete(redisOtpKey);
            return true;
        }
        return false;
    }

    public boolean verifyLoginOTP(String emailOrPhone, String otpEncrypt, String subSystem) {
        String redisOtpKey = RedisKeyUtil.getOtpKey(authConfig.getPrefixRedisKey(), subSystem, emailOrPhone);

        if (!aesService.decryptDataWithoutUrl(otpEncrypt).equals(redisTemplate.opsForValue().get(redisOtpKey)))
            return false;

        redisTemplate.delete(redisOtpKey);
        return true;
    }

    public boolean isEmailOrPhoneVerified(String emailOrPhone, Long subSystem) {
        String redisConfirmKey = RedisKeyUtil.redisConfirmMailOrPhoneKey(authConfig.getPrefixRedisKey(), emailOrPhone, subSystem);
        return OTPConstant.CONFIRMED_OTP.equals(redisTemplate.opsForValue().get(redisConfirmKey));
    }

    public ResponseEntity<ResponseMetaData> confirmPassword(Long userId, Long subSystem, ConfirmPassword confirmPassword) {
        log.info("SUCCESS while confirm password with userId = {}, subSystem = {}", userId, subSystem);

        if (redisAuthService.checkTimesPasswordFail(String.valueOf(userId), subSystem))
            return ResponseEntity.badRequest().body(new ResponseMetaData(
                    new MetaDTO(MetaData.PASSWORD_FAIL_TOO_MANY_TIMES), null));

        String currentPasswordDecrypt = aesService.decryptDataWithoutUrl(confirmPassword.getCurrentPassword());
        UserEntity userInfo = userRepository.findDistinctFirstByIdAndSubSystem(userId, subSystem);
        if (Objects.isNull(userInfo) || !UserStatus.ACTIVE.getValue().equalsIgnoreCase(userInfo.getStatus())) {
            log.warn("UserId = {} not exist in subSystem = {}", userId, subSystem);
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.USER_NOT_EXIST), null));
        }

        if (!encoder.matches(currentPasswordDecrypt, userInfo.getPassword())) {
            redisAuthService.countTimesPasswordFail(String.valueOf(userId), subSystem);
            log.warn("Password is incorrect with userId = {}, subSystem = {}", userId, subSystem);
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.PASSWORD_INCORRECT), null));
        }

        redisTemplate.opsForValue().set(
                RedisKeyUtil.redisConfirmPasswordKey(authConfig.getPrefixRedisKey(), userId, subSystem),
                CommonConstant.CONFIRMED, authConfig.getExpireTimeConfirmOtpOrPassword(), TimeUnit.MILLISECONDS);

        log.info("SUCCESS while confirm password with userId = {}, subSystem = {}", userInfo.getId(), subSystem);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), null));
    }

    @Transactional
    public ResponseEntity<ResponseMetaData> deleteUserAccount(Long userId, Long subSystem) {
        log.info("Start delete account with userId = {}, subSystem = {}", userId, subSystem);
        UserEntity account = userRepository.findDistinctFirstByIdAndSubSystem(userId, subSystem);
        if (Objects.isNull(account)) {
            log.warn("UserId = {} not exist in subSystem = {}", userId, subSystem);
            return ResponseEntity.badRequest().body(new ResponseMetaData(new MetaDTO(MetaData.USER_NOT_EXIST), null));
        }
        account.setStatus(UserStatus.REMOVED.getValue());
        userRepository.save(account);
        redisTemplate.delete(RedisKeyUtil.getUserStatusKey(authConfig.getPrefixRedisKey(), account.getId(), subSystem));
        deleteOldToken(String.valueOf(userId), subSystem);
        log.info("FINISH delete accountId = {} with userId = {}, subSystem = {}", account.getId(), userId, subSystem);
        return ResponseEntity.ok(new ResponseMetaData(new MetaDTO(MetaData.SUCCESS), null));
    }

    public void addVerifyUserPoemToGroupPublic(UserEntity user) {
        if (Objects.isNull(user) || SubSystem.isNotPoemAndPoemHK(user.getSubSystem()))
            return;
        log.info("Start auto join Verify user to public group with userId = {}", user.getId());
        List<CQGroupPost> groupPosts = mongoCQGroupPostRepository
                .findAllByOriginalSubSystemOrSubSystemContainsAndStatusInAndPrivacy(user.getSubSystem(),
                        CommonConstant.GROUP_STATUS_ACTIVE_AND_REPORTED,
                        GroupPrivacy.PUBLIC.getValue());
        MemberGroup member = new MemberGroup(user.getId(), GroupRole.MEMBER, MemberGroupStatus.ACTIVE);
        groupPosts.forEach(item -> autoJoinGroupWithUserP3(item, member, user.getStatus()));
        log.info("SUCCESS auto join Verify user to public group with userId = {}", user.getId());
    }

    public void addVerifyUserPoemToGroupOfTR(UserEntity user) {
        if (Objects.isNull(user)
                || SubSystem.isNotPoemAndPoemHK(user.getSubSystem())
                || ObjectUtils.anyNull(user.getListOfTRCodes(), user.getId())
                || NumberUtils.INTEGER_ZERO.equals(user.getListOfTRCodes().length))
            return;
        log.info("Start auto join Verify user to group of TR with userId = {}", user.getId());
        List<Long> listTRId = Arrays.asList(user.getListOfTRCodes());
        List<String> listSelfOfTR = userRepository.findAllByIdInAndSubSystem(listTRId, user.getSubSystem()).stream()
                .map(UserEntity::getSelfAeCd)
                .filter(StringUtils::isNotBlank)
                .distinct().collect(Collectors.toList());
        List<CQGroupPost> groupPosts = mongoCQGroupPostRepository
                .findByOriginalSubSystemOrSubSystemContainsAndSelfAeCdIn(user.getSubSystem(), listSelfOfTR);
        MemberGroup member = new MemberGroup(user.getId(), GroupRole.MEMBER, MemberGroupStatus.ACTIVE);
        groupPosts.forEach(item -> autoJoinGroupWithUserP3(item, member, user.getStatus()));
        log.info("SUCCESS auto join Verify user to group of TR with userId = {}", user.getId());
    }

    private void autoJoinGroupWithUserP3(CQGroupPost groupPost, MemberGroup memberGroup, String userStatus) {
        if (ObjectUtils.anyNull(groupPost, memberGroup) || CollectionUtils.isEmpty(groupPost.getMembers())) return;
        log.info("Start auto join user to group with userId = {}, groupId = {}",
                memberGroup.getUserId(), groupPost.getId());
        MemberGroup oldMember = groupPost.getMembers().stream()
                .filter(item -> Objects.equals(item.getUserId(), memberGroup.getUserId()))
                .findFirst().orElse(null);
        if (Objects.isNull(oldMember)) {
            groupPost.getMembers().add(memberGroup);
            groupPost.addOneToTotalMember(userStatus);
            MemberGroupInfo memberGroupInfo = new MemberGroupInfo(memberGroup.getUserId(), groupPost.getId());
            mongoTemplate.save(groupPost);
            mongoTemplate.save(memberGroupInfo);
            //TODO: push notification for new member
            return;
        }
        if (CommonConstant.MEMBER_STATUS_ACTIVE_MUTED_BLOCKED.contains(oldMember.getMemberGroupStatus()))
            return;
        int index = groupPost.getMembers().indexOf(oldMember);
        oldMember.setMemberGroupStatus(MemberGroupStatus.ACTIVE.getValue());
        groupPost.getMembers().set(index, oldMember);
        groupPost.addOneToTotalMember(userStatus);
        MemberGroupInfo memberGroupInfo = memberGroupInfoRepository
                .findMemberGroupInfoById(new MemberGroupInfo.IdInfo(oldMember.getUserId(), groupPost.getId()))
                .orElse(new MemberGroupInfo(oldMember.getUserId(), groupPost.getId()));
        memberGroupInfo.setBecomingMemberDate(Instant.now());
        mongoTemplate.save(groupPost);
        mongoTemplate.save(memberGroupInfo);
        //TODO: push notification for new member
        log.info("SUCCESS auto join user to group with userId = {}, groupId = {}",
                memberGroup.getUserId(), groupPost.getId());
    }

    @Transactional
    public void autoFollowingListTR(UserEntity user) {
        if (Objects.isNull(user)
                || SubSystem.isNotPoemAndPoemHK(user.getSubSystem())
                || ObjectUtils.anyNull(user.getListOfTRCodes(), user.getId())
                || NumberUtils.INTEGER_ZERO.equals(user.getListOfTRCodes().length))
            return;
        Long myId = user.getId();
        log.info("Start auto following of TR with myId = {}", myId);
        List<Long> listTRIdAndMe = Arrays.asList(user.getListOfTRCodes());
        listTRIdAndMe.add(myId);

        Map<Long, CQFriend> friendMap = addFriendListUser(myId, listTRIdAndMe);
        Map<Long, CQFollowRelation> followRelationMap = addFollowRelation(myId, listTRIdAndMe);
        friendMap.values().forEach(mongoTemplate::save);
        followRelationMap.values().forEach(mongoTemplate::save);

        log.info("SUCCESS auto following of TR with myId = {}", myId);
    }

    private Map<Long, CQFriend> addFriendListUser(Long myId, List<Long> listTRIdAndMe) {
        log.info("Start add friend list user with myId = {}", myId);
        if (CollectionUtils.isEmpty(listTRIdAndMe) || Objects.isNull(myId)) return new HashMap<>();

        Map<Long, CQFriend> friendMapInput = mongoCQFriendRepository.findAllByIdIn(listTRIdAndMe).stream()
                .collect(Collectors.toMap(CQFriend::getId, Function.identity()));

        Map<Long, CQFriend> friendMap = CollectionUtils.isEmpty(friendMapInput) ? new HashMap<>() : friendMapInput;
        CQFriendDetail meAsFriend = new CQFriendDetail(String.valueOf(myId), FriendType.FRIEND.getValue());
        List<CQFriendDetail> friendDetails = new ArrayList<>();
        for (Long id : listTRIdAndMe) {
            if (id.equals(myId)) continue;
            //Add me to list friend of other
            CQFriend friend = friendMap.getOrDefault(id, new CQFriend(id)).formatFriend();
            friend.getFriends().add(meAsFriend);
            friend.setFriends(ObjectUtil.distinctByLastKey(friend.getFriends(), CQFriendDetail::getId));
            friendMap.put(id, friend);

            //Add other to my friend list
            friendDetails.add(new CQFriendDetail(String.valueOf(id), FriendType.FRIEND.getValue()));
        }

        //Add list following to my following list
        CQFriend myFriend = friendMap.getOrDefault(myId, new CQFriend(myId)).formatFriend();
        myFriend.getFriends().addAll(friendDetails);
        myFriend.setFriends(ObjectUtil.distinctByLastKey(myFriend.getFriends(), CQFriendDetail::getId));
        friendMap.put(myId, myFriend);
        log.info("SUCCESS while add friend list user with myId = {}", myId);
        return friendMap;
    }

    private Map<Long, CQFollowRelation> addFollowRelation(Long myId, List<Long> listTRIdAndMe) {
        log.info("Start follow list user with myId = {}", myId);
        if (CollectionUtils.isEmpty(listTRIdAndMe) || Objects.isNull(myId)) return new HashMap<>();
        Map<Long, CQFollowRelation> followRelationMap =
                mongoCQFollowRelationRepository.getCQFollowRelationByIdIn(listTRIdAndMe).stream()
                        .collect(Collectors.toMap(CQFollowRelation::getId, Function.identity()));
        CQFollowerDetail meAsFollower = new CQFollowerDetail(String.valueOf(myId));
        List<CQFollowingDetail> followingDetails = new ArrayList<>();
        for (Long id : listTRIdAndMe) {
            if (id.equals(myId)) continue;
            //Add me to list follower of other
            CQFollowRelation followRelation = followRelationMap.getOrDefault(id, new CQFollowRelation(id))
                    .formatFollowRelation();
            followRelation.getFollowers().add(meAsFollower);
            followRelation.setFollowers(ObjectUtil.distinctByLastKey(followRelation.getFollowers(), CQFollowerDetail::getId));
            followRelationMap.put(id, followRelation);

            //Add other to my following list
            followingDetails.add(new CQFollowingDetail(String.valueOf(id), true));
        }
        //Add list following to my following list
        CQFollowRelation myFollowRelation = followRelationMap.getOrDefault(myId, new CQFollowRelation(myId))
                .formatFollowRelation();
        myFollowRelation.getFollowings().addAll(followingDetails);
        myFollowRelation.setFollowings(ObjectUtil.distinctByLastKey(myFollowRelation.getFollowings(), CQFollowingDetail::getId));
        followRelationMap.put(myId, myFollowRelation);

        log.info("SUCCESS follow list user with clientId = {}", myId);
        return followRelationMap;
    }
}
