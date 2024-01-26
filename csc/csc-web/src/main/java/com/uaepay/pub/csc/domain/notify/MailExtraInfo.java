package com.uaepay.pub.csc.domain.notify;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lzb
 */
@Data
public class MailExtraInfo {
    /** 附件 */
    private List<MailAttachment> attachments;

    public MailExtraInfo addAttachment(MailAttachment attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
        return this;
    }
}
