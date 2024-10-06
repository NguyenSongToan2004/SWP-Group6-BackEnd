package com.group4.FKitShop.Service;

import com.group4.FKitShop.Entity.Accounts;
import com.group4.FKitShop.Exception.AppException;
import com.group4.FKitShop.Exception.ErrorCode;
import com.group4.FKitShop.Repository.AccountsRepository;
import com.group4.FKitShop.Request.AccountsRequest;
import com.group4.FKitShop.Mapper.AccountsMapper;
import com.group4.FKitShop.Request.UpdateInfoCustomerRequest;
import com.group4.FKitShop.Request.UpdatePassword;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountsService {

    AccountsRepository accountsRepository;
    AccountsMapper accountsMapper;


    public Accounts register(AccountsRequest request) {
        if (accountsRepository.existsByemail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if (accountsRepository.existsByphoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        request.setRole("user");
        request.setStatus(1);
        Accounts accounts = accountsMapper.toAccounts(request);
        accounts.setCreateDate(new Date());
        //su dung brcrypt de ma hoa password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        accounts.setPassword(passwordEncoder.encode(request.getPassword()));
        return accountsRepository.save(accounts);
    }

    public List<Accounts> allAccounts() {
        return accountsRepository.findAll();
    }

    public List<Accounts> getActiveAccounts() {
        return accountsRepository.getActiveAccounts();
    }


    public Optional<Accounts> getAccountByID(String id) {
        return accountsRepository.findById(id);
    }

    public String updatePassword(UpdatePassword request, String id) {

        Accounts accounts = accountsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        accounts.setPassword(encoder.encode(request.getPassword()));

        accountsRepository.save(accounts);
        return "";
    }

    public Accounts updateInfo(String id, UpdateInfoCustomerRequest request) {
        Accounts accounts = accountsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );
        if (accountsRepository.existsByemail(request.getEmail()) && !request.getEmail().equals(accounts.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }

        if (accountsRepository.existsByphoneNumber(request.getPhoneNumber()) && !request.getPhoneNumber().equals(accounts.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        accountsMapper.toAccounts(request, accounts);
        return accountsRepository.save(accounts);
    }

    public String deleteAccount(String id) {
        Accounts accounts = accountsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );
        accounts.setStatus(0);
        accountsRepository.save(accounts);
        return "";
    }

    public String uploadAvatar(String id, MultipartFile image) {
        Accounts accounts = accountsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );
        String imageUrl = uploadImage(image);

        if (imageUrl != "") {
            accounts.setImage(imageUrl);
        }
        accountsRepository.save(accounts);
        return "";
    }

    public Accounts createAccount(AccountsRequest request) {
        if (accountsRepository.existsByemail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }
        if (accountsRepository.existsByphoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        request.setStatus(1);
        Accounts accounts = accountsMapper.toAccounts(request);
        accounts.setCreateDate(new Date());
        //su dung brcrypt de ma hoa password
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        accounts.setPassword(passwordEncoder.encode(request.getPassword()));
        return accountsRepository.save(accounts);
    }

    public Accounts updateAccount(AccountsRequest request, String id) {
        Accounts accounts = accountsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXIST)
        );

        if (accountsRepository.existsByemail(request.getEmail()) && !request.getEmail().equals(accounts.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXSITED);
        }

        if (accountsRepository.existsByphoneNumber(request.getPhoneNumber()) && !request.getPhoneNumber().equals(accounts.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        accountsMapper.toAccounts(request, accounts);
        return accountsRepository.save(accounts);
    }

    private static final String UPLOAD_DIRECTORY = "uploads" + File.separator + "accounts";

    String uploadImage(MultipartFile file) {
        // Kiểm tra xem file có rỗng không
        if (file.isEmpty()) {
            return "";
        }
        try {
            // Lấy đường dẫn tương đối đến thư mục uploads (có thể thay đổi tùy môi trường)
            String uploadDir = System.getProperty("user.dir") + File.separator + UPLOAD_DIRECTORY;
            // System.getProperty("user.dir") : lấy ra đường dẫn đến thư mục hiện tại
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdir();
            }
            // Lưu file vào thư mục
            String filePath = uploadDir + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));
            return UPLOAD_DIRECTORY + File.separator + file.getOriginalFilename();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }
    }


}
