package com.milkit.app.domain.user;

import java.time.LocalDateTime;
import java.util.*;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.milkit.app.config.jwt.JwtDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USERS")
@Entity
@Schema
public class User implements UserDetails, JwtDetails {
 
	@Id
	@Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "User 의 키ID")
	@JsonIgnore
    private Long id;
    
    @Column(name = "USER_ID", unique=true)
    @Schema(description = "사용자계정 ID")
    private String userId;
    
    @Column(name = "PASSWORD")
    @Schema(description = "사용자계정 비밀번호")
    private String password;

    @Column(name = "ROLE")
    @Schema(description = "사용자 구분", example = "(ROLE_MEMBER:사용자, ROLE_ADMIN:관리자)")
    private String role;
    
    @Column(name = "USE_YN")
	@Schema(description = "사용자 삭제여부")
    private String useYn;

	@Column(name = "DESCRIPTION")
	@Schema(description = "사용자 정보")
	private String description;

    @Column(name = "INST_TIME")
	@Schema(description = "사용자 등록시간")
	@JsonIgnore
    private LocalDateTime instTime;
	
    @Column(name = "UPD_TIME")
	@Schema(description = "사용자 갱신시간")
	@JsonIgnore
    private LocalDateTime updTime;
	
    @Column(name = "INST_USER")
	@Schema(description = "사용자 등록자")
	@JsonIgnore
    private String instUser;
    
    @Column(name = "UPD_USER")
	@Schema(description = "사용자 갱신자")
	@JsonIgnore
	private String updUser;	

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
        this.role = RoleEnum.MEMBER.getValue();
    }
	
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	@JsonIgnore
	public String getUsername() {
		return userId;
	}
	@JsonIgnore
	public void setUsername(String userId) {
		this.userId = userId;
	}
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return useYn != null && useYn.equals("Y");
	}
	
	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if(role.equals(RoleEnum.ADMIN.getValue())) {
        	grantedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.ADMIN.getValue()));
        } else {
        	grantedAuthorities.add(new SimpleGrantedAuthority(RoleEnum.MEMBER.getValue()));
        }
        
		return grantedAuthorities;
	}
	
	public static Collection<? extends GrantedAuthority> getAuthorities(String authority) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
       	grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        
		return grantedAuthorities;
	}
	
	@Override
	@JsonIgnore
	public String getSubject() {
		return userId;
	}
	@Override
	@JsonIgnore
	public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();

        claims.put("name", getUserId());
        claims.put("role", getRole());
        
        return claims;
	}
		
	@Override  
	public String toString() {
		return ToStringBuilder.reflectionToString(
				this, ToStringStyle.SHORT_PREFIX_STYLE
		);
	}
}
