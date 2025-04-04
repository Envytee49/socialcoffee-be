package com.example.socialcoffee.service;

import com.example.socialcoffee.configuration.ConfigResource;
import com.example.socialcoffee.dto.response.MetaDTO;
import com.example.socialcoffee.enums.MetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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

        if (checkNotAcceptedFileExtension(file))
            metaDTOList.add(new MetaDTO(MetaData.FILE_EXTENSION_NOT_ACCEPTED));
        return metaDTOList;
    }
}
