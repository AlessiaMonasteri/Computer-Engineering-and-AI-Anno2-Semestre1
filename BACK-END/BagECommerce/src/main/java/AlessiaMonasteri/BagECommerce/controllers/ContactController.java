package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.ContactCreateDTO;
import AlessiaMonasteri.BagECommerce.entities.Contact;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Subject;
import AlessiaMonasteri.BagECommerce.services.ContactService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // SEND (USER + ADMIN + SUPERADMIN)

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @PostMapping("/me")
    @ResponseStatus(HttpStatus.CREATED)
    public Contact create(@AuthenticationPrincipal User currentUser,
                          @RequestBody @Valid ContactCreateDTO dto) {
        return contactService.create(currentUser.getId(), dto);
    }

    // READ (SUPERADMIN)

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin")
    public List<Contact> getAll() {
        return contactService.findAll();
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/{id}")
    public Contact getById(@PathVariable UUID id) {
        return contactService.findById(id);
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/by-user/{userId}")
    public List<Contact> getByUser(@PathVariable UUID userId) {
        return contactService.findByUser(userId);
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/by-subject")
    public List<Contact> getBySubject(@RequestParam Subject subject) {
        return contactService.findBySubject(subject);
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @GetMapping("/superadmin/by-date")
    public List<Contact> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return contactService.findByDateRange(start, end);
    }
}