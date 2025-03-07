package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.DTO.UserRegistrationDTO;
import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Random;

/**
 * Kullanıcı kayıt işlemlerini yöneten controller sınıfı.
 */
@Controller
public class RegisterController {

    private final UserAccountServiceImpl userAccountServiceImpl;

    public RegisterController(UserAccountServiceImpl userAccountServiceImpl) {
        this.userAccountServiceImpl = userAccountServiceImpl;
    }

    /**
     * Kayıt sayfasını gösterir.
     * 
     * @param model View modeli
     * @return Kayıt sayfası
     */
    @GetMapping("/register")
    public String getRegisterPage(Model model){
        UserAccount account = new UserAccount();
        model.addAttribute("userAccount", account);
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "register";
    }

    /**
     * Yeni kullanıcı kaydı yapar ve doğrulama e-postası gönderir.
     * 
     * @param userRegistrationDTO Kullanıcı kayıt bilgileri
     * @param bindingResult Doğrulama sonuçları
     * @param model View modeli
     * @param redirectAttributes Yönlendirme özellikleri
     * @return Yönlendirme URL'si
     */
    @PostMapping("/register")
    public String registerNewUser(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO userRegistrationDTO, 
                                 BindingResult bindingResult, 
                                 Model model,
                                 RedirectAttributes redirectAttributes){
        System.out.println("Kayıt işlemi başlatılıyor...");
        System.out.println("E-posta: " + userRegistrationDTO.getEmail());
        
        if (bindingResult.hasErrors()) {
            System.out.println("Form doğrulama hataları var:");
            bindingResult.getAllErrors().forEach(error -> System.out.println(error.getDefaultMessage()));
            return "register";
        }

        // Kullanıcının zaten var olup olmadığını kontrol et
        if (userAccountServiceImpl.findByUsernameOrEmail(userRegistrationDTO.getEmail()).isPresent()) {
            System.out.println("Bu e-posta adresi zaten kayıtlı: " + userRegistrationDTO.getEmail());
            model.addAttribute("registrationError", "Bu e-posta adresi zaten kayıtlı. Lütfen başka bir e-posta adresi kullanın.");
            return "register";
        }

        try {
            System.out.println("Yeni kullanıcı oluşturuluyor...");
            UserAccount userAccount = new UserAccount();
            userAccount.setFirstName(userRegistrationDTO.getFirstName());
            userAccount.setLastName(userRegistrationDTO.getLastName());
            userAccount.setEmail(userRegistrationDTO.getEmail());
            userAccount.setPassword(userRegistrationDTO.getPassword()); // Şifre servis katmanında şifrelenecek
            userAccount.setNickname(userRegistrationDTO.getNickname());
            userAccount.setEnabled(false); // Doğrulama yapılana kadar hesap devre dışı
            userAccount.setActive(true);
            userAccount.setAccountNonLocked(true);
            userAccount.setFailedAttempt(0);
            
            // Doğrulama kodu oluştur
            String verificationCode = generateVerificationCode();
            userAccount.setVerificationCode(verificationCode);
            System.out.println("Doğrulama kodu oluşturuldu: " + verificationCode);
            
            // Kullanıcıyı kaydet
            System.out.println("Kullanıcı kaydediliyor...");
            userAccountServiceImpl.save(userAccount);
            System.out.println("Kullanıcı başarıyla kaydedildi. ID: " + userAccount.getId());
            
            // Doğrulama e-postası gönder
            try {
                System.out.println("Doğrulama e-postası gönderiliyor...");
                userAccountServiceImpl.sendVerificationCode(userAccount);
                System.out.println("Doğrulama e-postası başarıyla gönderildi.");
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Kayıt işleminiz başarıyla tamamlandı. Lütfen e-posta adresinize gönderilen doğrulama kodunu kullanarak hesabınızı aktifleştirin.");
                redirectAttributes.addAttribute("email", userAccount.getEmail());
                System.out.println("Kullanıcı doğrulama sayfasına yönlendiriliyor...");
                return "redirect:/verify-email";
            } catch (Exception e) {
                System.err.println("E-posta gönderimi sırasında hata: " + e.getMessage());
                e.printStackTrace();
                // Kullanıcı kaydedildi ama e-posta gönderilemedi, yine de doğrulama sayfasına yönlendir
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "Hesabınız oluşturuldu ancak doğrulama e-postası gönderilemedi. Lütfen e-posta ayarlarını kontrol edin veya yönetici ile iletişime geçin.");
                redirectAttributes.addAttribute("email", userAccount.getEmail());
                System.out.println("Kullanıcı doğrulama sayfasına yönlendiriliyor (e-posta hatası ile)...");
                return "redirect:/verify-email";
            }
        } catch (Exception e) {
            System.err.println("Kullanıcı kaydı sırasında hata: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("registrationError", "Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
            return "register";
        }
    }
    
    /**
     * 6 haneli rastgele bir doğrulama kodu oluşturur.
     * 
     * @return Doğrulama kodu
     */
    private String generateVerificationCode() {
        // 6 haneli rastgele bir doğrulama kodu oluştur
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
