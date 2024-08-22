package com.example.authority_example;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Getter
@Repository
public class EntityRepository {
    private final HashMap<String, UserEntity> db = new HashMap<>();

    public EntityRepository() {}

    /**
     * 사용자 이름으로 사용자를 찾습니다.
     *
     * @param name 찾을 사용자의 이름
     * @return 찾은 사용자 엔티티, 없으면 null
     */
    public UserEntity findByName(String name) {
        return db.get(name);
    }

    /**
     * 사용자를 삭제합니다.
     *
     * @param user 삭제할 사용자 엔티티
     * @return 삭제된 사용자 엔티티
     */
    public UserEntity deleteUser(UserEntity user) {
        return db.remove(user.getName());
    }

    /**
     * 사용자를 저장하거나 업데이트합니다.
     *
     * @param user 저장 또는 업데이트할 사용자 엔티티
     * @return 저장 또는 업데이트된 사용자 엔티티
     */
    public UserEntity save(UserEntity user) {
        return db.put(user.getName(), user);
    }

    /**
     * 모든 사용자의 이름 목록을 반환합니다.
     *
     * @return 모든 사용자의 이름 목록
     */
    public List<String> getAllUsernames() {
        return db.values().stream()
                .map(UserEntity::getName)
                .toList();
    }

    /**
     * 특정 부서에 속한 사용자의 이름 목록을 반환합니다.
     *
     * @param departmentName 조회할 부서 이름
     * @return 해당 부서에 속한 사용자의 이름 목록
     */
    public List<String> getUsernamesByDepartment(String departmentName) {
        return db.values().stream()
                .filter(user -> user.getDepartment().getName().equals(departmentName))
                .map(UserEntity::getName)
                .toList();
    }
}
