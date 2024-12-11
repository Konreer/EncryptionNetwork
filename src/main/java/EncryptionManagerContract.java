import DTO.UserDTO;
import DTO.DataDTO;
import converters.InstantConverter;
import entry.DataEntry;
import entry.UserEntry;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Contract(
        name = "EncryptionManagerContract",
        info = @Info(
                title = "EncryptionManagerContract",
                description = "EncryptionManagerContract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "plasota.konrad@gmail.com",
                        name = "Konrad Plasota")))
@Default
public final class EncryptionManagerContract implements ContractInterface {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void storeData(final Context ctx,
                          String username,
                          String data,
                          String password,
                          String salt,
                          String note,
                          String serviceName,
                          String tagsAsString) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        UserEntry entry = retrieveUserEntry(ctx, username);
        if (entry == null) {
            entry = new UserEntry(username);
        }

        DataEntry dataEntry = new DataEntry(
                encrypt(data, password, salt),
                salt,
                note,
                serviceName,
                stub.getTxTimestamp(),
                stub.getTxTimestamp(),
                Arrays.stream(tagsAsString.split(",")).collect(Collectors.toList()),
                new ArrayList<>());
        entry.addDataEntry(dataEntry);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create();
        String entryJson = gson.toJson(entry);

        CompositeKey key = stub.createCompositeKey("EncryptionManager", username);
        stub.putStringState(key.toString(), entryJson);
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String retrieveData(
            final Context ctx,
            String username,
            String password) {
        UserEntry entry = retrieveUserEntry(ctx, username);

        if (entry == null) {
            return null;
        }

        UserDTO response = new UserDTO(entry);

        try {
            for(DataEntry dataEntry : entry.getDataEntrySet()) {
                response.addPassword(new DataDTO(
                        decrypt(dataEntry.getEncryptedData(), password, dataEntry.getSalt()),
                        dataEntry));
            }
        //return nothing if decryption fails
        } catch (Exception e) {
            return null;
        }

        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create().toJson(response);
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void deleteData(final Context ctx, String username, String password) {
        ChaincodeStub stub = ctx.getStub();
        UserEntry entry = retrieveUserEntry(ctx, username);
        if (entry == null) {
            return;
        }

        Optional<DataEntry> dataEntryOptional = entry.getDataEntrySet()
                .stream()
                .findFirst();

        if (!dataEntryOptional.isPresent())
        {
            throw new ChaincodeException("User not found");
        }
        DataEntry dataEntry = dataEntryOptional.get();
        try {
            decrypt(dataEntry.getEncryptedData(), password, dataEntry.getSalt());
        } catch (Exception e) {
            throw new ChaincodeException("User not found");
        }

        CompositeKey key = stub.createCompositeKey("EncryptionManager", username);
        stub.delState(key.toString());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void updateData(final Context ctx,
                           String username,
                           String data,
                           String password,
                           String note,
                           String serviceName,
                           String tagsAsString) throws Exception {
        ChaincodeStub stub = ctx.getStub();
        UserEntry entry = retrieveUserEntry(ctx, username);
        if (entry == null) {
            return;
        }

        Optional<DataEntry> dataEntryOptional = entry.getDataEntrySet()
                .stream()
                .filter(pswEntry -> pswEntry.getServiceName().equals(serviceName))
                .findFirst();

        if (!dataEntryOptional.isPresent())
        {
            throw new ChaincodeException("Entry not found");
        }
         DataEntry dataEntry = dataEntryOptional.get();
        try {
            decrypt(dataEntry.getEncryptedData(), password, dataEntry.getSalt());
        } catch (Exception e) {
            throw new ChaincodeException("Entry not found");
        }

        dataEntry.updateData(
                encrypt(data, password, dataEntry.getSalt()),
                note,
                Arrays.stream(tagsAsString.split(",")).collect(Collectors.toList()),
                stub.getTxTimestamp());

        entry.addDataEntry(dataEntry);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create();
        String entryJson = gson.toJson(entry);

        CompositeKey key = stub.createCompositeKey("EncryptionManager", username);
        stub.putStringState(key.toString(), entryJson);
    }

    public UserEntry retrieveUserEntry(final Context ctx, String username) {
        ChaincodeStub stub = ctx.getStub();
        CompositeKey key = stub.createCompositeKey("EncryptionManager", username);
        String entryJson = stub.getStringState(key.toString());
        if (entryJson == null || entryJson.isEmpty()) {
            return null;
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantConverter())
                .create();

        return gson.fromJson(entryJson, UserEntry.class);
    }

    public static String encrypt(String data, String password, String salt) throws Exception {
        // Generate a random IV
        byte[] iv = generateIV(password);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Generate key from password
        SecretKey secretKey = getKeyFromPassword(password, salt);

        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        // Perform encryption
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedIvAndText = new byte[IV_SIZE + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIvAndText, 0, IV_SIZE);
        System.arraycopy(encryptedBytes, 0, encryptedIvAndText, IV_SIZE, encryptedBytes.length);

        // Encode to Base64
        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    private static SecretKey getKeyFromPassword(String password, String salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public static String decrypt(String encryptedData, String password, String salt) throws Exception {
        // Decode from Base64
        byte[] encryptedIvAndText = Base64.getDecoder().decode(encryptedData);

        // Extract IV
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(encryptedIvAndText, 0, iv, 0, IV_SIZE);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted text
        byte[] encryptedBytes = new byte[encryptedIvAndText.length - IV_SIZE];
        System.arraycopy(encryptedIvAndText, IV_SIZE, encryptedBytes, 0, encryptedBytes.length);

        // Generate key from password
        SecretKey secretKey = getKeyFromPassword(password, salt);

        // Initialize cipher
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        // Perform decryption
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] generateIV(String password) throws Exception {
        // Use a hashing algorithm to generate a deterministic IV from the password
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(hash, 0, iv, 0, IV_SIZE);
        return iv;
    }
}

