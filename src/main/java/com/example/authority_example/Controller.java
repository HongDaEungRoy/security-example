package com.example.authority_example;

import com.example.authority_example.Requests.CreateUserRequest;
import com.example.authority_example.Requests.DeleteUserRequest;
import com.example.authority_example.Requests.LoginRequest;
import com.example.authority_example.Requests.UpdateUserRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class Controller {
    private final AuthService authService;
    private final EntityRepository entityRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 시 사용자 정보가 담긴 token을 반환합니다
     *
     * @param loginRequest
     * @return
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    /**
     * 현재 인증된 사용자의 정보를 반환합니다.
     * 'USER' 역할 이상의 모든 인증된 사용자가 접근 가능합니다.
     *
     * @param user 현재 인증된 사용자의 상세 정보
     * @return 사용자 이름
     */
    @GetMapping("/self")
    public String getSelf(@AuthenticationPrincipal CustomUserDetails user) {
        return user.getUsername();
    }

    /**
     * 관리자 정보를 반환합니다.
     * 'ADMIN' 역할을 가진 사용자만 접근 가능합니다.
     *
     * @param user 현재 인증된 관리자의 상세 정보
     * @return 관리자 이름
     */
    @GetMapping("/admin")
    public String getAdmin(@AuthenticationPrincipal CustomUserDetails user) {
        return "Admin: " + user.getUsername();
    }

    /**
     * 수퍼 관리자 정보를 반환합니다.
     * 'SUPER_ADMIN' 역할을 가진 사용자만 접근 가능합니다.
     *
     * @param user 현재 인증된 수퍼 관리자의 상세 정보
     * @return 수퍼 관리자 이름
     */
    @GetMapping("/super-admin")
    public String getSuperAdmin(@AuthenticationPrincipal CustomUserDetails user) {
        return "Super Admin: " + user.getUsername();
    }

    /**
     * 하이퍼 수퍼 관리자 정보를 반환합니다.
     * 'HYPER_SUPER_ADMIN' 역할을 가진 사용자만 접근 가능합니다.
     *
     * @param user 현재 인증된 하이퍼 수퍼 관리자의 상세 정보
     * @return 하이퍼 수퍼 관리자 이름
     */
    @GetMapping("/hyper-super-admin")
    public String getHyperSuperAdmin(@AuthenticationPrincipal CustomUserDetails user) {
        return "Hyper Super Admin: " + user.getUsername();
    }

    /**
     * 모든 사용자의 목록을 반환합니다.
     * 'ADMIN', 'SUPER_ADMIN', 'HYPER_SUPER_ADMIN' 역할을 가진 사용자가 접근 가능합니다.
     *
     * @return 모든 사용자의 이름 목록
     */
    @GetMapping("/users")
    @PreAuthorize(AuthorizeConstants.GET_USER_ALL_AUTHORITY)
    public ResponseEntity<List<String>> getAllUsers() {
        List<String> usernames = entityRepository.getAllUsernames();
        return ResponseEntity.ok(usernames);
    }

    /**
     * 특정 부서에 속한 사용자의 목록을 반환합니다.
     * 'SUPER_ADMIN', 'HYPER_SUPER_ADMIN' 역할을 가진 사용자가 접근 가능합니다.
     *
     * @param departmentName 조회할 부서 이름
     * @return 해당 부서에 속한 사용자의 이름 목록
     */
    @GetMapping("/users/department/{departmentName}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HYPER_SUPER_ADMIN')")
    public ResponseEntity<List<String>> getUsersByDepartment(@PathVariable String departmentName) {
        List<String> usernames = entityRepository.getUsernamesByDepartment(departmentName);
        return ResponseEntity.ok(usernames);
    }

    /**
     * 특정 사용자의 정보를 업데이트합니다.
     * 'SUPER_ADMIN', 'HYPER_SUPER_ADMIN' 역할을 가진 사용자가 접근 가능합니다.
     *
     * @param username 업데이트할 사용자의 이름
     * @param updateUserRequest 업데이트할 사용자 정보
     * @return 업데이트 성공 여부에 대한 응답
     */
    @PutMapping("/user/{username}")
    @PreAuthorize("hasAuthority('UPDATE')")
    public ResponseEntity<String> updateUser(@PathVariable String username, @RequestBody UpdateUserRequest updateUserRequest) {
        UserEntity user = entityRepository.findByName(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.updateName(updateUserRequest.getNewName());
        entityRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    /**
     * 새로운 사용자를 생성합니다.
     * 'HYPER_SUPER_ADMIN' 역할을 가진 사용자만 접근 가능합니다.
     *
     * @param createUserRequest 생성할 사용자 정보
     * @return 사용자 생성 성공 여부에 대한 응답
     */
    @PostMapping("/user")
    @PreAuthorize("hasRole('HYPER_SUPER_ADMIN')")
    public ResponseEntity<String> addNewUser(@RequestBody CreateUserRequest createUserRequest) {
        if (entityRepository.findByName(createUserRequest.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        DepartmentEntity department = new DepartmentEntity(createUserRequest.getDepartment());
        RoleEntity role = new RoleEntity(RoleCode.valueOf(createUserRequest.getRole()), List.of(new AuthorityEntity(AuthorityCode.READ, "Read permission")));
        UserEntity newUser = new UserEntity(createUserRequest.getUsername(), createUserRequest.getPassword(), department, List.of(role));
        newUser.encodePassword(passwordEncoder);
        entityRepository.save(newUser);
        return ResponseEntity.ok("User created successfully");
    }

    /**
     * 특정 사용자를 삭제합니다.
     * 'HYPER_SUPER_ADMIN' 역할을 가진 사용자만 접근 가능하며,
     * 삭제하려는 사용자와 같은 부서에 속한 경우에만 삭제가 가능합니다.
     *
     * @param deleteUserRequest 삭제할 사용자 정보
     * @return 사용자 삭제 성공 여부에 대한 응답
     */
    @DeleteMapping("/user")
    @PreAuthorize(AuthorizeConstants.DELETE_USER_AUTHORITY)
    public ResponseEntity<String> deleteUser(@RequestBody DeleteUserRequest deleteUserRequest) {
        UserEntity userEntity = entityRepository.findByName(deleteUserRequest.getUsername());
        if (userEntity == null) {
            return ResponseEntity.notFound().build();
        }
        entityRepository.deleteUser(userEntity);
        return ResponseEntity.ok("User deleted successfully: " + userEntity.getName());
    }

    @PostConstruct
    public void createUsers() {
        // Create authorities
        AuthorityEntity readAuthority = new AuthorityEntity(AuthorityCode.READ, "Read permission");
        AuthorityEntity writeAuthority = new AuthorityEntity(AuthorityCode.WRITE, "Write permission");
        AuthorityEntity updateAuthority = new AuthorityEntity(AuthorityCode.UPDATE, "Update permission");
        AuthorityEntity deleteAuthority = new AuthorityEntity(AuthorityCode.DELETE, "Delete permission");

        // Create roles with appropriate authorities
        RoleEntity userRole = new RoleEntity(RoleCode.USER, List.of(readAuthority));
        RoleEntity adminRole = new RoleEntity(RoleCode.ADMIN, List.of(readAuthority, writeAuthority));
        RoleEntity superAdminRole = new RoleEntity(RoleCode.SUPER_ADMIN, List.of(readAuthority, writeAuthority, updateAuthority));
        RoleEntity hyperSuperAdminRole = new RoleEntity(RoleCode.HYPER_SUPER_ADMIN, List.of(readAuthority, writeAuthority, updateAuthority, deleteAuthority));

        // Create departments
        DepartmentEntity backendDept = new DepartmentEntity("백엔드");
        DepartmentEntity frontendDept = new DepartmentEntity("프론트엔드");
        DepartmentEntity fullstackDept = new DepartmentEntity("풀스택");
        DepartmentEntity adminDept = new DepartmentEntity("백엔드");

        // Create users
        createAndSaveUser("user김", "password123", backendDept, List.of(userRole));
        createAndSaveUser("admin이", "password456", frontendDept, List.of(adminRole, userRole));
        createAndSaveUser("superadmin박", "password789", fullstackDept, List.of(superAdminRole, adminRole, userRole));
        createAndSaveUser("hypersuperadmin최", "passwordabc", adminDept, List.of(hyperSuperAdminRole, superAdminRole, adminRole, userRole));
    }

    private void createAndSaveUser(String name, String password, DepartmentEntity department, List<RoleEntity> roles) {
        UserEntity user = new UserEntity(name, password, department, roles);
        user.encodePassword(passwordEncoder);
        entityRepository.save(user);
    }
}
