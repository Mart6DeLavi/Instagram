package com.instagram.postservice.document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Media {
    private String url;
    private String type;
}
