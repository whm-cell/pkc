package com.esay.soacserver.entity;

import com.esay.soacserver.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author ltq
 * @since 2019-08-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;

    private List<GrantedAuthority> authorities;
}
