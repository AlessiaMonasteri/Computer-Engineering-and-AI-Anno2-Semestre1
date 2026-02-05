package AlessiaMonasteri.BagECommerce.controllers;

import AlessiaMonasteri.BagECommerce.DTO.NewUserDTO;
import AlessiaMonasteri.BagECommerce.DTO.UpdateUserRoleDTO;
import AlessiaMonasteri.BagECommerce.entities.User;
import AlessiaMonasteri.BagECommerce.entities.enums.Role;
import AlessiaMonasteri.BagECommerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // USER + ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @GetMapping("/me")
    public User me(@AuthenticationPrincipal User currentUser) {
        return userService.findById(currentUser.getId());
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN','SUPERADMIN')")
    @PutMapping("/me")
    public User updateMe(@AuthenticationPrincipal User currentUser,
                         @RequestBody NewUserDTO body) {
        return userService.updateSelf(currentUser.getId(), body);
    }

    // ADMIN + SUPERADMIN

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin")
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/{userId}")
    public User getUserById(@PathVariable UUID userId) {
        return userService.findById(userId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPERADMIN')")
    @GetMapping("/admin/by-role")
    public List<User> getUsersByRole(@RequestParam Role role) {
        return userService.findUsersByRole(role);
    }

    // SUPERADMIN

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PatchMapping("/superadmin/{userId}/role")
    public User updateRole(@PathVariable UUID userId,
                           @RequestBody @Valid UpdateUserRoleDTO body) {
        return userService.updateUserRole(userId, body);
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @DeleteMapping("/superadmin/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable UUID userId) {
        userService.findByIdAndDelete(userId);
    }
}
