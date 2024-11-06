package com.friends.security.entity

import jakarta.persistence.*

@Entity
class Member(
    @Id @GeneratedValue
    val id: Long? = null,  // id는 불변 값으로 설정하여 JPA에서 자동으로 할당
    var name: String,

    @Column(unique = true, updatable = false) // 이메일은 중복되지 않아야 하며 수정되지 않아야 합니다.
    val email: String,
    var password: String,

    @Enumerated(EnumType.STRING)
    var oauth2Provider: OAuth2Provider,

    var imageUrl: String,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val authorities: MutableList<Authority> = ArrayList()  // 권한 리스트는 기본적으로 비어 있는 리스트로 초기화
) : BaseTimeEntity() {

    companion object {
        /**
         * 일반 사용자 생성 메서드
         * 권한을 ROLE_USER로 초기화하여 Member 객체를 반환합니다.
         */
        fun createUser(
            name: String,
            email: String,
            password: String,
            oauth2Provider: OAuth2Provider,
            oauth2Id: Int,
            imageUrl: String
        ): Member {
            return Member(
                name = name,
                email = email,
                password = password,
                oauth2Provider = oauth2Provider,
                imageUrl = imageUrl
            ).apply {
                addAuthority(Role.ROLE_USER)  // 기본 권한을 사용자 권한으로 설정
            }
        }

        /**
         * 관리자 생성 메서드
         * 권한을 ROLE_USER와 ROLE_ADMIN으로 초기화하여 Member 객체를 반환합니다.
         */
        fun createAdmin(
            name: String,
            email: String,
            password: String,
            oauth2Provider: OAuth2Provider,
            oauth2Id: Int,
            imageUrl: String
        ): Member {
            return Member(
                name = name,
                email = email,
                password = password,
                oauth2Provider = oauth2Provider,
                imageUrl = imageUrl
            ).apply {
                addAuthority(Role.ROLE_USER)  // 기본 권한으로 사용자 권한 추가
                addAuthority(Role.ROLE_ADMIN) // 관리자 권한 추가
            }
        }
    }

    /**
     * 새로운 권한을 추가하는 메서드
     * 중복된 권한을 방지하고 권한 추가 로직을 캡슐화합니다.
     */
    fun addAuthority(role: Role) {
        // 이미 동일한 권한이 존재하는지 검사 후 추가
        if (authorities.none { it.role == role }) {
            authorities.add(Authority(role = role, member = this))
        }
    }
}

@Entity
class Authority(
    @Id @GeneratedValue
    val id: Long? = null,  // id는 불변 값으로 설정하여 JPA에서 자동으로 할당
    @Enumerated(EnumType.STRING)
    var role: Role,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: Member
) : BaseTimeEntity()
