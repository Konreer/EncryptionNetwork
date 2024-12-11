package DTO;

import entry.PasswordEntry;
import org.hyperledger.fabric.contract.annotation.Property;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class PasswordDTO implements Serializable {
    private final String password;
    private final String note;
    private final String serviceName;
    private final Instant creationTimestamp;
    private final Instant updateTimestamp;
    private final List<String> tags;
    private final List<String> passwordHistory;

    public PasswordDTO(String password,
                       String note,
                       String serviceName,
                       Instant creationTimestamp,
                       Instant updateTimestamp,
                       List<String> tags,
                       List<String> passwordHistory) {
        this.password = password;
        this.note = note;
        this.serviceName = serviceName;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
        this.passwordHistory = passwordHistory;
    }

    public PasswordDTO(String password, PasswordEntry passwordEntry) {
        this.password = password;
        this.note = passwordEntry.getNote();
        this.serviceName = passwordEntry.getServiceName();
        this.creationTimestamp = passwordEntry.getCreationTimestamp();
        this.updateTimestamp = passwordEntry.getUpdateTimestamp();
        this.tags = passwordEntry.getTags();
        this.passwordHistory = passwordEntry.getPasswordHistory();
    }

    public String getPassword() {
        return password;
    }

    public String getNote() {
        return note;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getPasswordHistory() {
        return passwordHistory;
    }
}
