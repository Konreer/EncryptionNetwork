package DTO;

import entry.UserEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDTO implements Serializable {
    private final String username;
    private final List<DataDTO> data;

    public UserDTO(String username) {
        this.username = username;
        this.data = new ArrayList<>();
    }

    public UserDTO(UserEntry userEntry) {
        this.username = userEntry.getUsername();
        this.data = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void addPassword(DataDTO dataDTO) {
        this.data.add(dataDTO);
    }
}
