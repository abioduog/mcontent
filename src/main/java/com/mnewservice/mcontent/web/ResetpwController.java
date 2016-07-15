package com.mnewservice.mcontent.web;

import com.mnewservice.mcontent.messaging.MessageCenter;
import com.mnewservice.mcontent.domain.EmailMessage;
import com.mnewservice.mcontent.domain.Email;
import com.mnewservice.mcontent.util.exception.MessagingException;

import com.mnewservice.mcontent.domain.ProviderInfo;
import com.mnewservice.mcontent.manager.ResetpwManager;
import com.mnewservice.mcontent.domain.Resetpw;


import com.mnewservice.mcontent.manager.UserManager;
import com.mnewservice.mcontent.domain.User;

import com.mnewservice.mcontent.manager.ProviderManager;
import com.mnewservice.mcontent.domain.Provider;

import com.mnewservice.mcontent.security.PasswordEncrypter;
import java.util.Calendar;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author Pasi Saukkonen <pasi.saukkonen at nolwenture.com>
 */
@Controller
public class ResetpwController {

    private static final Logger LOG
            = Logger.getLogger(ResetpwController.class);
    
    @Autowired
    ProviderManager providerManager;
    
    @Autowired
    ResetpwManager resetpwManager;
        
    @Autowired
    UserManager userManager;
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${application.host}")
    private String applicationHost;
    
    @Value("${application.host.port}")
    private String applicationHostPort;
    
    
    private static final String MESSAGE_START_SENDING
            = "START: Sending message '%s' from %s to %s";
    private static final String MESSAGE_END_SENDING = "END: Sending message";


    @RequestMapping(value="/login/confirmpw", method=RequestMethod.GET)
    public String listServices1(@RequestParam("user") String checksum, Model model){
        //System.out.println("Checksum: " + checksum);
        Resetpw resetpw = resetpwManager.findByChecksum(checksum);
        Provider provider = providerManager.findByUserId(resetpw.getUserid());
        String emailaddress = provider.getEmail();
        
        if(resetpw.getExpires().before(Calendar.getInstance().getTime())){
            model.addAttribute("emailaddress", "");
            model.addAttribute("found", null);
            model.addAttribute("oldlink", true);
            return "resetPw";
        }
        
        model.addAttribute("user", checksum);
        model.addAttribute("emailaddress", emailaddress);
 
        return "confirmPw";
    }
    
    @RequestMapping(value="/login/confirmpw", method=RequestMethod.POST)
    public String storeNewPassword(@RequestParam("user") String checksum, @RequestParam("password") String password, Model model){
        Resetpw resetpw = resetpwManager.findByChecksum(checksum);

        User user = userManager.getUser(resetpw.getUserid());
        user.setPassword(PasswordEncrypter.getInstance().encrypt(password));
        userManager.saveUser(user);
        model.addAttribute("passwordchanged", true);
       
        return "login";
        }
    
    
    @RequestMapping(value="/login/resetpw", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("emailaddress", new String(""));
        return "resetPw";
    }
    
    @RequestMapping(value="login/resetpw", method=RequestMethod.POST)
    public String greetingSubmit(@RequestParam("emailaddress") String emailaddress, Model model) {

      Provider provider = providerManager.findByEmail(emailaddress);


        //If found...
        // 1) put info to resetpw-table
        // 2) send url email to given address
        model.addAttribute("emailaddress", emailaddress);
        model.addAttribute("found", (provider != null));

        if(provider != null){
       
            StringBuffer resetpwlinkmessage = new StringBuffer();
            String checksum = getChecksumForResetLink(provider.getEmail(), provider.getUser().getId());

            try{

                resetpwlinkmessage.append("\nReset your password from the link below.");
                resetpwlinkmessage.append("\n");
                resetpwlinkmessage.append("http://"+applicationHost+":"+applicationHostPort+"/mContent/login/confirmpw?user=" + checksum);
                //System.out.println("Url to reset password: " + resetpwlinkmessage.toString());

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom("mContent");
                mailMessage.setTo(provider.getEmail());
                mailMessage.setSubject("Password reset");
                mailMessage.setText(resetpwlinkmessage.toString());
                javaMailSender.send(mailMessage);

            }catch(Exception me){
                String msg = me.getMessage();
                LOG.error(msg);
            } finally {
                LOG.debug(MESSAGE_END_SENDING);
            }
        
        }
        
        return "resetPw";
        
    }
    
        private String getChecksumForResetLink(String email, long useridnro) {
        // TODO code application logic here
        String userid = Long.toString(useridnro);
        int MINUTESFUTURE = 15;
        String checksum = "";
               
        Calendar cal = Calendar.getInstance();
        Date thismoment = cal.getTime();
        cal.add(Calendar.MINUTE, 15);
        
        SimpleDateFormat format = new SimpleDateFormat();
        format = new SimpleDateFormat("yyyyMMddHHmmss");
        
        String DateToFuture = format.format(cal.getTime());

        System.out.println("Now: " + format.format(thismoment));
        System.out.println("Future " + MINUTESFUTURE + " min: " + DateToFuture);
                
        // Always fbdfe5e8ae87a6c827109fee4c142eea
        checksum = getMD5((DateToFuture + email + userid));
        System.out.println("Dynamic: " + checksum);

           
        Resetpw resetpw = new Resetpw();
        resetpw.setChecksum(checksum);
        resetpw.setExpires(cal.getTime());
        resetpw.setUserid(Integer.decode(userid).intValue());
        System.out.println("Tallennetaan käyttäjä: " +resetpw.getChecksum() + ", " + resetpw.getExpires() + ", " + resetpw.getUserid());
        resetpwManager.saveResetpw(resetpw);

        return checksum;
        
    }
        private String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
}
