package AlessiaMonasteri.BagECommerce.DTO;

import AlessiaMonasteri.BagECommerce.entities.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleDTO(@NotNull Role role) {}

