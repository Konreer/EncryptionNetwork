package entry;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@DataType()
public class PasswordEntry {
    @Property()
    private String encryptedPassword;
    @Property()
    private String salt;
    @Property()
    private String note;
    @Property()
    private String serviceName;
    @Property()
    private Instant creationTimestamp;
    @Property()
    private Instant updateTimestamp;
    @Property()
    private List<String> tags;
    @Property()
    private List<String> passwordHistory;


    public PasswordEntry(String encryptedPassword,
                         String salt,
                         String note,
                         String serviceName,
                         Instant creationTimestamp,
                         Instant updateTimestamp,
                         List<String> tags,
                         List<String> passwordHistory) {
        this.encryptedPassword = encryptedPassword;
        this.note = note;
        this.serviceName = serviceName;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
        this.passwordHistory = passwordHistory;
        this.salt = salt;
    }
    public void updatePassword(String password,
                               String note,
                               List<String> tags,
                               Instant updateTimestamp) {
        passwordHistory.add(encryptedPassword);

        this.encryptedPassword = password;
        this.note = note;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Instant creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Instant getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Instant updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getPasswordHistory() {
        return passwordHistory;
    }

    public void setPasswordHistory(List<String> passwordHistory) {
        this.passwordHistory = passwordHistory;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PasswordEntry that = (PasswordEntry) o;
        return Objects.equals(salt, that.salt) && Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salt, serviceName);
    }
}
