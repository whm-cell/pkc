package com.esay.soacserver.entity;

import com.esay.soacserver.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class RolePermission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private Long permissionId;


}
