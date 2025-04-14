package com.peakmeshop.api.dto;

import java.util.Map;

public record EmailSendDTO(
    String to,
    String[] cc,
    String[] bcc,
    String subject,
    String content,
    boolean isHtml,
    Map<String, byte[]> attachments
) {} 