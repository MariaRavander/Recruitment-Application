package recruitment.presentation;

import org.springframework.security.core.userdetails.UserDetailsService;
import recruitment.application.UserDetailsServiceImpl;
import recruitment.domain.FieldAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import recruitment.application.RecruitmentService;
import recruitment.domain.Person;
import recruitment.domain.PersonDTO;

import javax.validation.Valid;

/**
 * Handles all http requests to the root
 */
@Controller
@Scope("session")
public class RecruitmentController {
    static final String DEFAULT_PAGE_URL = "/";
    static final String REGISTER_URL = "register";
    static final String LOGIN_URL = "login";
    static final String SUCCESSFUL_REGISTRATION = "success-registration";
    static final String APPLICANT_HOME_URL = "applicant-home";
    static final String RECRUITER_HOME_URL = "recruiter-home";

    private static final String CREATE_PERSON_OBJ_NAME = "createPersonForm";

    @Autowired
    private UserDetailsServiceImpl service;

    private PersonDTO currentPers;

    /**
     * Redirect to default page for logged in user, if not logged in directed to login page
     * @return default page URL
     */
    @GetMapping(DEFAULT_PAGE_URL)
    public String showDefaultView() {
        return "redirect:" + REGISTER_URL;
    }

    /**
     * Get request for login page
     * @return login URL
     */
    @GetMapping("/"+LOGIN_URL)
    public String showLoginView(){
        return LOGIN_URL;
    }


    /**
     * Get request for register page
     * @return register URL
     */
    @GetMapping("/" + REGISTER_URL)
    public String showRegisterView(CreatePersonForm createPersonForm) {
        return REGISTER_URL;
    }



    @GetMapping("/" + APPLICANT_HOME_URL)
    public String applicantHomeView(Model model){
        PersonDTO person = service.findLoggedInUser();
        if(person==null){
            return LOGIN_URL;
        }
        model.addAttribute("person",person);

        return APPLICANT_HOME_URL;
    }

    @GetMapping("/" + RECRUITER_HOME_URL)
    public String recruiterHomeView(Model model){
        PersonDTO person = service.findLoggedInUser();
        if(person==null){
            return LOGIN_URL;
        }
        model.addAttribute("person",person);

        return RECRUITER_HOME_URL;
    }
    /**
     * Post request for a registration form
     * @param createPersonForm content of the registration form
     * @param bindingResult validation result
     * @param model object used if validation fails
     * @return Successful registration URL on successful registration otherwise register URL with validation errors
     */
    @PostMapping("/" + "registerPerson")
    public String registerPerson(@Valid CreatePersonForm createPersonForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println("i binding error");
            return REGISTER_URL;
        }

        String firstName = createPersonForm.getFirstName();
        String lastName = createPersonForm.getLastName();
        String ssn = createPersonForm.getSsn();
        String mail = createPersonForm.getMail();
        String password = createPersonForm.getPassword();

        try {
            service.createPerson(firstName, lastName, ssn, mail, password);
        } catch (FieldAlreadyExistException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return REGISTER_URL;
        }
        return SUCCESSFUL_REGISTRATION;
    }
}