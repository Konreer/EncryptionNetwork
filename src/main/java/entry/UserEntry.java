package entry;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.HashSet;
import java.util.Set;

@DataType()
public final class UserEntry {
    @Property()
    private final String username;
    @Property()
    private Set<DataEntry> dataEntrySet;

    public UserEntry(String username, Set<DataEntry> dataEntrySet) {
        this.username = username;
        this.dataEntrySet = dataEntrySet;
    }

    public UserEntry(String username) {
        this.username = username;
        this.dataEntrySet = new HashSet<>();
    }

    public Set<DataEntry> getDataEntrySet() {
        return dataEntrySet;
    }

    public void addDataEntry(DataEntry dataEntry) {
        this.dataEntrySet.add(dataEntry);
    }

    public void setDataEntrySet(HashSet<DataEntry> dataEntrySet) {
        this.dataEntrySet = dataEntrySet;
    }

    public String getUsername() {
        return username;
    }


}