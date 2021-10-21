package kz.capitalpay.server.login.mapper;

import kz.capitalpay.server.login.dto.ApplicationUserResponseDto;
import kz.capitalpay.server.login.model.ApplicationUser;
import org.springframework.stereotype.Component;

@Component
public class ApplicationUserMapper {

    public ApplicationUserResponseDto convertToResponseDto(ApplicationUser user) {
        ApplicationUserResponseDto dto = new ApplicationUserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setStatus(ApplicationUserResponseDto.getUserStatus(user));
        dto.setUsername(user.getUsername());
        dto.setTimestamp(user.getTimestamp());
        dto.setRealname(user.getRealname());
        return dto;
    }
}
