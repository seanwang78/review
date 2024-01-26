package com.uaepay.pub.csc.domain.notify;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author lzb
 */
@Data
@AllArgsConstructor
public class MailAttachment {
    /** File name */
    @NotBlank
    private String fileName;

    /** Reference url */
    @NotBlank
    private String refUrl;
}
