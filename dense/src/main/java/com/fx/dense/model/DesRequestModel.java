package com.fx.dense.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesRequestModel {

    private String type;

    private String context;

    private String key;

    private String encryptionMode;

    private String filling;

    private String characterSet;

    private String output;

    public String getBuildMode(){
        return this.type+"/"+this.getEncryptionMode()+"/"+this.getFilling();
    }

}
