package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.ConfigResource;
import com.example.socialcoffee.dto.request.UpdateNewPassword;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.enums.MetaData;
import com.example.socialcoffee.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationService {
    private final ConfigResource configResource;
    public List<MetaDTO> validationCommentPost(String content, MultipartFile[] file, boolean isCreateComment) {
        List<MetaDTO> metaDTOList = new ArrayList<>();
        content = StringUtils.trimToEmpty(content);

        if (isCreateComment && StringUtils.isEmpty(content) && ObjectUtils.isEmpty(file)) {
            metaDTOList.add(new MetaDTO(MetaData.CONTENT_AND_FILE_MISSING));
        }
        if (content.length() > configResource.getMaxLengthCommentPost()) {
            metaDTOList.add(new MetaDTO(MetaData.EXCEED_MAX_LENGTH_COMMENT_POST.getMetaCode(),
                    String.format(MetaData.EXCEED_MAX_LENGTH_COMMENT_POST.getMessage(), configResource.getMaxLengthCommentPost())));
        }

//        if (checkNotAcceptedFileExtension(file))
//            metaDTOList.add(new MetaDTO(MetaData.FILE_EXTENSION_NOT_ACCEPTED));
        return metaDTOList;
    }

    public List<MetaDTO> validateUpdateNewPassword(UpdateNewPassword updateNewPassword) {
        List<MetaDTO> metaList = new ArrayList<>();
        if (StringUtils.isBlank(updateNewPassword.getNewPassword())
                || !PasswordUtils.isPassword(updateNewPassword.getNewPassword())) {
            log.warn("New password is invalid");
            metaList.add(new MetaDTO(MetaData.PASSWORD_INVALID));
        }

        if (StringUtils.isBlank(updateNewPassword.getCurrentPassword())) {
            log.warn("Param current password is missing");
            metaList.add(new MetaDTO(MetaData.PASSWORD_MISSING));
        }

        return metaList;
    }
}
