package DTO;

import entry.DataEntry;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class DataDTO implements Serializable {
    private final String data;
    private final String note;
    private final String serviceName;
    private final Instant creationTimestamp;
    private final Instant updateTimestamp;
    private final List<String> tags;
    private final List<String> passwordHistory;

    public DataDTO(String data,
                   String note,
                   String serviceName,
                   Instant creationTimestamp,
                   Instant updateTimestamp,
                   List<String> tags,
                   List<String> passwordHistory) {
        this.data = data;
        this.note = note;
        this.serviceName = serviceName;
        this.creationTimestamp = creationTimestamp;
        this.updateTimestamp = updateTimestamp;
        this.tags = tags;
        this.passwordHistory = passwordHistory;
    }

    public DataDTO(String data, DataEntry dataEntry) {
        this.data = data;
        this.note = dataEntry.getNote();
        this.serviceName = dataEntry.getServiceName();
        this.creationTimestamp = dataEntry.getCreationTimestamp();
        this.updateTimestamp = dataEntry.getUpdateTimestamp();
        this.tags = dataEntry.getTags();
        this.passwordHistory = dataEntry.getDataHistory();
    }

    public String getData() {
        return data;
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
