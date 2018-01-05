package vsp.encrypted_key;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Encrypted_Key {

    @JsonCreator
    public Encrypted_Key(String HMACK, String algorithm, String encryption, String hint, String key, String key_encoding, String keylength, String message, String mode, String padding) {
        this.HMACK = HMACK;
        this.algorithm = algorithm;
        this.encryption = encryption;
        this.hint = hint;
        this.key = key;
        this.key_encoding = key_encoding;
        this.keylength = keylength;
        this.message = message;
        this.mode = mode;
        this.padding = padding;
    }

    @Id
    @GeneratedValue
    private Long id;

    private String HMACK;

    private String algorithm;

    private String encryption;

    private String hint;

    private String key;

    private String key_encoding;

    private String keylength;

    private String message;

    private String mode;

    private String padding;

    public String getHMACK() {
        return HMACK;
    }

    public void setHMACK(String HMACK) {
        this.HMACK = HMACK;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey_encoding() {
        return key_encoding;
    }

    public void setKey_encoding(String key_encoding) {
        this.key_encoding = key_encoding;
    }

    public String getKeylength() {
        return keylength;
    }

    public void setKeylength(String keylength) {
        this.keylength = keylength;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }
}
