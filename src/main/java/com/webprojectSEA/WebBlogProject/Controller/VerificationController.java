package com.webprojectSEA.WebBlogProject.Controller;

import com.webprojectSEA.WebBlogProject.Model.UserAccount;
import com.webprojectSEA.WebBlogProject.Services.UserServices.UserAccountServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Random;

/**
 * E-posta doğrulama işlemlerini yöneten controller sınıfı.
 */
@Controller
public class VerificationController {

    private final UserAccountServiceImpl userAccountServiceImpl;

    public VerificationController(UserAccountServiceImpl userAccountServiceImpl) {
        this.userAccountServiceImpl = userAccountServiceImpl;
    }

    /**
     * E-posta doğrulama sayfasını gösterir.
     * 
     * @param email Doğrulanacak e-posta adresi
     * @param model View modeli
     * @return Doğrulama sayfası
     */
    @GetMapping("/verify-email")
    public String showVerificationPage(@RequestParam(required = false) String email, Model model) {
        System.out.println("Doğrulama sayfası gösteriliyor...");
        System.out.println("E-posta: " + email);
        
        model.addAttribute("email", email);
        return "verificationCode";
    }
    
    /**
     * Kullanıcının girdiği doğrulama kodunu kontrol eder.
     * 
     * @param email E-posta adresi
     * @param verificationCode Doğrulama kodu
     * @param redirectAttributes Yönlendirme özellikleri
     * @return Yönlendirme URL'si
     */
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("email") String email, 
                             @RequestParam("verificationCode") String verificationCode,
                             RedirectAttributes redirectAttributes) {
        boolean verified = userAccountServiceImpl.verify(verificationCode);
        
        if (verified) {
            redirectAttributes.addFlashAttribute("successMessage", "Hesabınız başarıyla doğrulandı. Şimdi giriş yapabilirsiniz.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Doğrulama kodu geçersiz veya süresi dolmuş.");
            redirectAttributes.addAttribute("email", email);
            return "redirect:/verify-email";
        }
    }
    
    /**
     * Yeni bir doğrulama kodu oluşturup gönderir.
     * 
     * @param email E-posta adresi
     * @param redirectAttributes Yönlendirme özellikleri
     * @return Yönlendirme URL'si
     */
    @PostMapping("/resend-verification")
    public String resendVerificationCode(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        try {
            UserAccount userAccount = userAccountServiceImpl.findByEmail(email);
            
            // Yeni doğrulama kodu oluştur
            String verificationCode = generateVerificationCode();
            userAccount.setVerificationCode(verificationCode);
            userAccountServiceImpl.save(userAccount);
            
            // Doğrulama e-postası gönder
            userAccountServiceImpl.sendVerificationCode(userAccount);
            
            redirectAttributes.addFlashAttribute("successMessage", "Doğrulama kodu tekrar gönderildi. Lütfen e-posta kutunuzu kontrol edin.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Doğrulama kodu gönderilirken bir hata oluştu: " + e.getMessage());
        }
        
        redirectAttributes.addAttribute("email", email);
        return "redirect:/verify-email";
    }
    
    /**
     * 6 haneli rastgele bir doğrulama kodu oluşturur.
     * 
     * @return Doğrulama kodu
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
} 