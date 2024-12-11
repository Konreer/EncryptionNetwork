package entry;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@DataType()
public class DataEntry {
    @Property()
    private String encryptedData;
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
    private List<String> dataHistory;


    public DataEntry(String encryptedData,
                     String salt,
                     String note,
                     String serviceName,
                     Instant creationTimestamp,
                     Instant updateTimestamp,
                     List<String> tags,
                     List<String> dataHistory) {
        this.encryptedData = encryptedData;
        this.note = note;
        this.serviceName = serviceName;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
        this.dataHistory = dataHistory;
        this.salt = salt;
    }
    public void updateData(String encryptedData,
                           String note,
                           List<String> tags,
                           Instant updateTimestamp) {
        dataHistory.add(this.encryptedData);

        this.encryptedData = encryptedData;
        this.note = note;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
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

    public List<String> getDataHistory() {
        return dataHistory;
    }

    public void setDataHistory(List<String> dataHistory) {
        this.dataHistory = dataHistory;
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
        DataEntry that = (DataEntry) o;
        return Objects.equals(salt, that.salt) && Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(salt, serviceName);
    }
}
