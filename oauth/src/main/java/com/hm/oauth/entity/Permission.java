package com.hm.oauth.entity;

import com.hm.oauth.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

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
public class Permission extends BaseEntity implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    private String url;

    private String name;

    private String description;

    private Long pid;

    @Override
    public String getAuthority() {
        return url;
    }
}
