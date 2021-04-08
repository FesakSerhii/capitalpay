package kz.capitalpay.server.login.dto;

import java.util.List;

public class ChangeRolesDTO {
    Long userId;
    List<String> roleList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }
}
