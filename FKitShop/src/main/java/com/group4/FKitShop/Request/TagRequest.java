package com.group4.FKitShop.Request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TagRequest {

    @Size(min = 1, max = 100, message = "Tag name must not more than 100 characters")
    private String tagName;
    private String description;
    private int status;

    // crud blogtag
    List<String> blogID;

}
